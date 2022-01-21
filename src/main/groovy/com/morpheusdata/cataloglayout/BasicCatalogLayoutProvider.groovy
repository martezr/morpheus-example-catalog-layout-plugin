package com.morpheusdata.cataloglayout

import com.morpheusdata.core.AbstractCatalogItemLayoutProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.model.Account
import com.morpheusdata.model.CatalogItemType
import com.morpheusdata.model.TaskConfig
import com.morpheusdata.model.ContentSecurityPolicy
import com.morpheusdata.model.User
import com.morpheusdata.views.HTMLResponse
import com.morpheusdata.views.ViewModel

class BasicCatalogLayoutProvider extends AbstractCatalogItemLayoutProvider {
	Plugin plugin
	MorpheusContext morpheus

	String code = 'basic-catalog-layout'
	String name = 'Basic Catalog Layout'

	BasicCatalogLayoutProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheus = context
	}

	@Override
	HTMLResponse renderTemplate(CatalogItemType catalogItemType, User user) {
		ViewModel<CatalogItemType> model = new ViewModel<>()
		println "Catalog Item: $catalogItemType"
		
		def HashMap<String, String> catalogItemPayload = new HashMap<String, String>();

		def webnonce = morpheus.getWebRequest().getNonceToken()
		catalogItemPayload.put("catalogItem", catalogItemType)
		catalogItemPayload.put("webnonce",webnonce)
		model.object = catalogItemPayload
		getRenderer().renderTemplate("hbs/basicCatalogItem", model)
	}

	@Override
	ContentSecurityPolicy getContentSecurityPolicy() {
		def csp = new ContentSecurityPolicy()
		csp
	}
}
