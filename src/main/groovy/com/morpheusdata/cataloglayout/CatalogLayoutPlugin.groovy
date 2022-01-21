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
		this.setName("Example Catalog Layout Plugin")
		this.setDescription("Example plugin for customizing self-service catalog items")
		this.setAuthor("Martez Reed")
		this.setSourceCodeLocationUrl("https://github.com/martezr/morpheus-example-catalog-layout-plugin")
		this.setIssueTrackerUrl("https://github.com/martezr/morpheus-example-catalog-layout-plugin/issues")
		this.setPermissions([Permission.build('Example Catalog Layout','example-catalog-view', [Permission.AccessType.none, Permission.AccessType.full])])
        }
	@Override
	void onDestroy() {}
}
