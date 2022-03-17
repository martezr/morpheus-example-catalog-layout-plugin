package com.morpheusdata.cataloglayout

import com.morpheusdata.core.Plugin
import com.morpheusdata.model.Permission
import com.morpheusdata.views.HandlebarsRenderer
import com.morpheusdata.views.ViewModel
import com.morpheusdata.web.Dispatcher
import com.morpheusdata.model.OptionType

class CatalogLayoutPlugin extends Plugin {

	@Override
	String getCode() {
		return 'example-catalog-layout-plugin'
	}

	@Override
	void initialize() {
		BasicCatalogLayoutProvider basicCatalogLayoutProvider = new BasicCatalogLayoutProvider(this, morpheus)
		this.pluginProviders.put(basicCatalogLayoutProvider.code, basicCatalogLayoutProvider)
		QaasCatalogLayoutProvider qaasCatalogLayoutProvider = new QaasCatalogLayoutProvider(this, morpheus)
		this.pluginProviders.put(qaasCatalogLayoutProvider.code, qaasCatalogLayoutProvider)
		QaasTabProvider qaasTabProvider = new QaasTabProvider(this, morpheus)
		this.pluginProviders.put(qaasTabProvider.code, qaasTabProvider)
		DbaasCatalogLayoutProvider dbaasCatalogLayoutProvider = new DbaasCatalogLayoutProvider(this, morpheus)
		this.pluginProviders.put(dbaasCatalogLayoutProvider.code, dbaasCatalogLayoutProvider)		
		this.setName("Example Catalog Layout Plugin")
		this.setDescription("Example plugin for customizing self-service catalog items")
		this.setAuthor("Martez Reed")
		this.setSourceCodeLocationUrl("https://github.com/martezr/morpheus-example-catalog-layout-plugin")
		this.setIssueTrackerUrl("https://github.com/martezr/morpheus-example-catalog-layout-plugin/issues")
		this.setPermissions([Permission.build('Example Catalog Layout','example-catalog-view', [Permission.AccessType.none, Permission.AccessType.full])])

		// Plugin settings the are used to configure the behavior of the plugin
		// https://developer.morpheusdata.com/api/com/morpheusdata/model/OptionType.html
		this.settings << new OptionType(
			name: 'RabbitMQ Host',
			code: 'rabbitmq-host',
			fieldName: 'rabbitHost',
			displayOrder: 0,
			fieldLabel: 'Host',
			helpText: 'The RabbitMQ URL (http://192.168.2.45:15672',
			required: true,
			inputType: OptionType.InputType.TEXT
		)

		this.settings << new OptionType(
			name: 'RabbitMQ Admin Username',
			code: 'rabbitmq-admin-username',
			fieldName: 'rabbitAdminUsername',
			displayOrder: 1,
			fieldLabel: 'Username',
			helpText: 'The RabbitMQ admin username',
			required: true,
			inputType: OptionType.InputType.TEXT
		)

		this.settings << new OptionType(
			name: 'RabbitMQ Admin Password',
			code: 'rabbitmq-admin-password',
			fieldName: 'rabbitAdminPassword',
			displayOrder: 2,
			fieldLabel: 'Password',
			helpText: 'The RabbitMQ admin password',
			required: true,
			inputType: OptionType.InputType.PASSWORD
		)
        }
	@Override
	void onDestroy() {}
}
