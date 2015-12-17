/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.watcher.actions.slack.service;

import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.watcher.shield.WatcherSettingsFilter;
import org.elasticsearch.watcher.support.http.HttpClient;

/**
 *
 */
public class InternalSlackService extends AbstractLifecycleComponent<InternalSlackService> implements SlackService {

    private final HttpClient httpClient;
    public static final Setting<Settings> SLACK_ACCOUNT_SETTING = Setting.groupSetting("watcher.actions.slack.service.", true, Setting.Scope.CLUSTER);
    private volatile SlackAccounts accounts;

    @Inject
    public InternalSlackService(Settings settings, HttpClient httpClient, ClusterSettings clusterSettings, WatcherSettingsFilter settingsFilter) {
        super(settings);
        this.httpClient = httpClient;
        settingsFilter.filterOut("watcher.actions.slack.service.account.*.url");
        clusterSettings.addSettingsUpdateConsumer(SLACK_ACCOUNT_SETTING, this::setSlackAccountSetting);
    }

    @Override
    protected void doStart() {
        setSlackAccountSetting(SLACK_ACCOUNT_SETTING.get(settings));
    }

    @Override
    protected void doStop() {
    }

    @Override
    protected void doClose() {
    }

    @Override
    public SlackAccount getDefaultAccount() {
        return accounts.account(null);
    }

    private void setSlackAccountSetting(Settings setting) {
        accounts = new SlackAccounts(setting, httpClient, logger);
    }

    @Override
    public SlackAccount getAccount(String name) {
        return accounts.account(name);
    }


}
