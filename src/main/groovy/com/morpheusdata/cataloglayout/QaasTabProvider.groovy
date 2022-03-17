package com.morpheusdata.cataloglayout

import com.morpheusdata.core.AbstractInstanceTabProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.cypher.CypherAccess
import com.morpheusdata.model.Account
import com.morpheusdata.model.Instance
import com.morpheusdata.model.TaskConfig
import com.morpheusdata.model.ContentSecurityPolicy
import com.morpheusdata.model.User
import com.morpheusdata.views.HTMLResponse
import com.morpheusdata.views.ViewModel
import com.morpheusdata.core.util.RestApiUtil
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.reactivex.Single
import groovy.util.logging.Slf4j


class QaasTabProvider extends AbstractInstanceTabProvider {
	Plugin plugin
	MorpheusContext morpheus
	RestApiUtil qaasAPI

	String code = 'qaas-tab'
	String name = 'QaaS'

	QaasTabProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheus = context
		this.qaasAPI = new RestApiUtil()
	}

	QaasTabProvider(Plugin plugin, MorpheusContext morpheusContext, RestApiUtil api, Account account) {
		this.morpheusContext = morpheusContext
		this.plugin = plugin
		this.qaasAPI = api
	}

	@Override
	HTMLResponse renderTemplate(Instance instance) {

		// Instantiate an object for storing data
		// passed to the html template
		ViewModel<Instance> model = new ViewModel<>()
		
		// Retrieve additional details about the instance
        // https://developer.morpheusdata.com/api/com/morpheusdata/model/TaskConfig.InstanceConfig.html
		TaskConfig instanceDetails = morpheus.buildInstanceConfig(instance, [:], null, [], [:]).blockingGet()

		println "customOptions ${instanceDetails.customOptions["virtualHostName"]}"

        // Create a payload to pass to the HTML template		
		def HashMap<String, String> instanceTabPayload = new HashMap<String, String>();

        // Retrieve plugin settings
		def settings = morpheus.getSettings(plugin)
		def settingsOutput = ""
		settings.subscribe(
			{ outData -> 
                 settingsOutput = outData
        	},
        	{ error ->
                 println error.printStackTrace()
        	}
		)

		// Parse the plugin settings payload. The settings will be available as
		// settingsJson.$optionTypeFieldName i.e. - settingsJson.ddApiKey to retrieve the DataDog API key setting
		JsonSlurper slurper = new JsonSlurper()
		def settingsJson = slurper.parseText(settingsOutput)

		def results = qaasAPI.callApi(settingsJson.rabbitHost, "api/queues/${instanceDetails.customOptions["virtualHostName"]}", settingsJson.rabbitAdminUsername, settingsJson.rabbitAdminPassword, new RestApiUtil.RestOptions(headers:['Content-Type':'application/json'], ignoreSSL: false), 'GET')
		//def results = qaasAPI.callApi(settingsJson.rabbitHost, "api/queues/${instanceDetails.customOptions["virtualHostName"]}", "guest", "guest", new RestApiUtil.RestOptions(headers:['Content-Type':'application/json'], ignoreSSL: false), 'GET')

		// Parse the JSON response payload returned
		// from the REST API call.
		def json = slurper.parseText(results.content)

        def numberOfQueues = json.size
		instanceTabPayload.put("numberOfQueues", numberOfQueues)
		instanceTabPayload.put("queues", json)

		def vhostResults = qaasAPI.callApi(settingsJson.rabbitHost, "api/vhosts/${instanceDetails.customOptions["virtualHostName"]}", settingsJson.rabbitAdminUsername, settingsJson.rabbitAdminPassword, new RestApiUtil.RestOptions(headers:['Content-Type':'application/json'], ignoreSSL: false), 'GET')

		// Parse the JSON response payload returned
		// from the REST API call.
		def vhostPayload = slurper.parseText(vhostResults.content)

		instanceTabPayload.put("name", vhostPayload.name)
		instanceTabPayload.put("tracing", vhostPayload.tracing)

        // Add the catalog item to the payload object
		instanceTabPayload.put("instanceDetails", instanceDetails)

        // Fetch the web nonce to align with the content security policy (CSP)
		// to enable the execution of the JavaScript script and add
		// it to the payload object used by the HTML template
		def webnonce = morpheus.getWebRequest().getNonceToken()
		instanceTabPayload.put("webnonce",webnonce)
		
		model.object = instanceTabPayload

        // Render the HTML template located in 
        // resources/renderer/hbs/instanceTab.hbs
        getRenderer().renderTemplate("hbs/qaasTab", model)
	}


	// This method contains the logic for when the tab
	// should be displayed in the UI
	@Override
	Boolean show(Instance instance, User user, Account account) {
		// Set the tab to not be shown be default
		def show = false
		def log
		//log.info("does it work for QaaS tab")
		// Retrieve additional details about the instance
		TaskConfig config = morpheus.buildInstanceConfig(instance, [:], null, [], [:]).blockingGet()
		//println "tenant ${config.tenant}"
		//println "instance details ${config.instance.createdByUsername}"
		println "customOptions ${config.customOptions}"
		def data = instance.instanceTypeCode
		//log.info("instance type code ${instance.instanceTypeCode}")
		println "instance layout name ${instance.layoutName}"

		if (data == "rabbitmq-virtual-host"){
			show = true
		}
		return show
	}

	/**
	 * Allows various sources used in the template to be loaded
	 * @return
	 */
	@Override
	ContentSecurityPolicy getContentSecurityPolicy() {
		def csp = new ContentSecurityPolicy()
		csp
	}
}