//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.seata.spring.boot.autoconfigure.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties(
        prefix = "seata"
)
@EnableConfigurationProperties({SpringCloudAlibabaConfiguration.class})
public class SeataProperties {
    private boolean enabled = true;
    private String applicationId;
    private String txServiceGroup;
    private boolean enableAutoDataSourceProxy = false;
    private String dataSourceProxyMode = "AT";
    private boolean useJdkProxy = false;
    private String[] excludesForAutoProxying = new String[0];
    @Autowired
    private SpringCloudAlibabaConfiguration springCloudAlibabaConfiguration;

    public SeataProperties() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SeataProperties setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getApplicationId() {
        if (this.applicationId == null) {
            this.applicationId = this.springCloudAlibabaConfiguration.getApplicationId();
        }

        return this.applicationId;
    }

    public SeataProperties setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getTxServiceGroup() {
        if (this.txServiceGroup == null) {
            this.txServiceGroup = this.springCloudAlibabaConfiguration.getTxServiceGroup();
        }

        return this.txServiceGroup;
    }

    public SeataProperties setTxServiceGroup(String txServiceGroup) {
        this.txServiceGroup = txServiceGroup;
        return this;
    }

    public boolean isEnableAutoDataSourceProxy() {
        return this.enableAutoDataSourceProxy;
    }

    public SeataProperties setEnableAutoDataSourceProxy(boolean enableAutoDataSourceProxy) {
        this.enableAutoDataSourceProxy = enableAutoDataSourceProxy;
        return this;
    }

    public String getDataSourceProxyMode() {
        return this.dataSourceProxyMode;
    }

    public void setDataSourceProxyMode(String dataSourceProxyMode) {
        this.dataSourceProxyMode = dataSourceProxyMode;
    }

    public boolean isUseJdkProxy() {
        return this.useJdkProxy;
    }

    public SeataProperties setUseJdkProxy(boolean useJdkProxy) {
        this.useJdkProxy = useJdkProxy;
        return this;
    }

    public String[] getExcludesForAutoProxying() {
        return this.excludesForAutoProxying;
    }

    public SeataProperties setExcludesForAutoProxying(String[] excludesForAutoProxying) {
        this.excludesForAutoProxying = excludesForAutoProxying;
        return this;
    }
}
