/*****************************************************************************************
* Copyright (C) 2023-2023  Ricardo Leoneti
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
*
* Contributors:
*     Ricardo Leoneti <ricardo.leoneti@gmail.com>    - initial API and implementation
*****************************************************************************************/
package org.leoneti.dbeaver.redash.ui.views;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBConstants;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.connection.DBPConnectionConfiguration;
import org.jkiss.dbeaver.model.impl.preferences.AbstractPreferenceStore;
import org.jkiss.dbeaver.ui.IDialogPageProvider;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.dialogs.connection.ConnectionPageAbstract;
import org.jkiss.dbeaver.ui.dialogs.connection.DriverPropertiesDialogPage;
import org.jkiss.dbeaver.ui.internal.UIConnectionMessages;
import org.jkiss.utils.CommonUtils;
import org.leoneti.dbeaver.redash.ui.internal.Activator;
import org.leoneti.dbeaver.redash.ui.internal.RedashMessages;

/**
 * RedashConnectionPage
 */
public class RedashConnectionPage extends ConnectionPageAbstract implements IDialogPageProvider {
    private static final Log log = Log.getLog(RedashConnectionPage.class);
    private static final ImageDescriptor logoImage = Activator.getImageDescriptor("icons/redash_logo.png");

    private Text portText;
    private Text hostText;
    private Text databaseText;
    private Text tokenText;
    private Button checkBox;
    protected Button saveTokenCheck;

    @Override
    public void createControl(Composite composite) {
        // Composite group = new Composite(composite, SWT.NONE);
        // group.setLayout(new GridLayout(1, true));
        setImageDescriptor(logoImage);

        ModifyListener textListener = e -> evaluateURL();
        Listener updateListener = e -> evaluateURL();


        Composite addrGroup = new Composite(composite, SWT.NONE);
        GridLayout gl = new GridLayout(1, false);
        addrGroup.setLayout(gl);
        GridData gd = new GridData(GridData.FILL_BOTH);
        addrGroup.setLayoutData(gd);

        Group hostGroup = UIUtils.createControlGroup(addrGroup, RedashMessages.redash_connection_page_label_connection, 2, GridData.FILL_HORIZONTAL, 0);
        hostText = UIUtils.createLabelText(hostGroup, RedashMessages.redash_connection_page_label_host, null);
        hostText.addModifyListener(textListener);

        Group authGroup = UIUtils.createControlGroup(addrGroup, UIConnectionMessages.dialog_connection_auth_group, 2, GridData.FILL_HORIZONTAL, 0);
        tokenText = createPasswordText(authGroup, RedashMessages.redash_connection_page_label_token);
        tokenText.addModifyListener(textListener);

        Group optionalGroup = UIUtils.createControlGroup(addrGroup, RedashMessages.redash_connection_page_label_optional, 2, GridData.FILL_HORIZONTAL, 0);
        databaseText = UIUtils.createLabelText(optionalGroup, RedashMessages.redash_connection_page_label_database, null);
        databaseText.addModifyListener(textListener);
        checkBox = UIUtils.createCheckbox(optionalGroup, RedashMessages.redash_connection_page_label_ssl, true);
        checkBox.addListener(SWT.Selection, updateListener);

        //createAuthPanel(addrGroup, 1);
        createDriverPanel(addrGroup);
        setControl(addrGroup);
    }

    @Override
    public boolean isComplete() {
        return hostText != null && tokenText != null
                && !CommonUtils.isEmpty(hostText.getText()) 
                && !CommonUtils.isEmpty(tokenText.getText());
    }

    @Override
    public void loadSettings() {
        // Load values from new connection info
        DBPDataSourceContainer activeDataSource = site.getActiveDataSource();
        DBPConnectionConfiguration connectionInfo = activeDataSource.getConnectionConfiguration();
        if (!CommonUtils.isEmpty(connectionInfo.getHostName())) {
            hostText.setText(CommonUtils.notEmpty(connectionInfo.getHostName()));
        } else {
            hostText.setText(DBConstants.HOST_LOCALHOST);
        }
        if (!CommonUtils.isEmpty(connectionInfo.getUserPassword())) {
            tokenText.setText(CommonUtils.notEmpty(connectionInfo.getUserPassword()));
        }
        if (databaseText != null) {
            if (site.isNew() && CommonUtils.isEmpty(connectionInfo.getDatabaseName())) {
                databaseText.setText(CommonUtils.notEmpty(site.getDriver().getDefaultDatabase()));
            } else {
                databaseText.setText(CommonUtils.notEmpty(connectionInfo.getDatabaseName()));
            }
        }

        checkBox.setSelection(true);
        if( site.isNew() ) {
            String encryptComm = connectionInfo.getProviderProperty("ssl");
            if (encryptComm != null && !Boolean.parseBoolean(encryptComm) ) {
                checkBox.setSelection(false);
            }
        } else {
            String encryptComm = connectionInfo.getProperty("ssl");
            if (encryptComm != null && !Boolean.parseBoolean(encryptComm) ) {
                checkBox.setSelection(false);
            }
        }
        super.loadSettings();
    }

    @Override
    public void saveSettings(DBPDataSourceContainer dataSource) {
        DBPConnectionConfiguration connectionInfo = dataSource.getConnectionConfiguration();
        if (hostText != null) {
            connectionInfo.setHostName(hostText.getText().trim());
        }
        if (tokenText != null) {
            connectionInfo.setUserPassword(tokenText.getText().trim());
        }
        if (databaseText != null) {
            connectionInfo.setDatabaseName(databaseText.getText().trim());
        }
        if (checkBox != null) {
            connectionInfo.setProperty("ssl", checkBox.getSelection() ? AbstractPreferenceStore.TRUE : AbstractPreferenceStore.FALSE );
        }
        super.saveSettings(dataSource);
    }

    @Override
    public IDialogPage[] getDialogPages(boolean extrasOnly, boolean forceCreate) {
        return new IDialogPage[] { new DriverPropertiesDialogPage(this) };
    }

    private void evaluateURL() {
        site.updateButtons();
    }

}
