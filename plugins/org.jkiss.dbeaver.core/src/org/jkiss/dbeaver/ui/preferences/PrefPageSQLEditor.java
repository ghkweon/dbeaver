/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2016 Serge Rieder (serge@jkiss.org)
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (version 2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jkiss.dbeaver.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.jkiss.dbeaver.DBeaverPreferences;
import org.jkiss.dbeaver.core.CoreMessages;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPPreferenceStore;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.editors.sql.SQLEditor;
import org.jkiss.dbeaver.ui.editors.sql.SQLPreferenceConstants;
import org.jkiss.dbeaver.utils.PrefUtils;

/**
 * PrefPageSQLEditor
 */
public class PrefPageSQLEditor extends TargetPrefPage
{
    public static final String PAGE_ID = "org.jkiss.dbeaver.preferences.main.sqleditor"; //$NON-NLS-1$

    private Button editorSeparateConnectionCheck;
    private Button connectOnActivationCheck;
    private Button connectOnExecuteCheck;

    private Button saveOnQueryExecution;
    private Button autoSaveOnClose;

    // Auto-close
    private Button acSingleQuotesCheck;
    private Button acDoubleQuotesCheck;
    private Button acBracketsCheck;
    // Auto-Format
    private Button afKeywordCase;
    private Button afExtractFromSource;

    private Button autoFoldersCheck;
    private Text scriptTitlePattern;

    public PrefPageSQLEditor()
    {
        super();
    }

    @Override
    protected boolean hasDataSourceSpecificOptions(DBPDataSourceContainer dataSourceDescriptor)
    {
        DBPPreferenceStore store = dataSourceDescriptor.getPreferenceStore();
        return
            store.contains(DBeaverPreferences.EDITOR_SEPARATE_CONNECTION) ||
            store.contains(DBeaverPreferences.EDITOR_CONNECT_ON_ACTIVATE) ||
            store.contains(DBeaverPreferences.EDITOR_CONNECT_ON_EXECUTE) ||
    
            store.contains(SQLPreferenceConstants.AUTO_SAVE_ON_CLOSE) ||
            store.contains(SQLPreferenceConstants.AUTO_SAVE_ON_EXECUTE) ||
    
            store.contains(SQLPreferenceConstants.SQLEDITOR_CLOSE_SINGLE_QUOTES) ||
            store.contains(SQLPreferenceConstants.SQLEDITOR_CLOSE_DOUBLE_QUOTES) ||
            store.contains(SQLPreferenceConstants.SQLEDITOR_CLOSE_BRACKETS) ||
            store.contains(SQLPreferenceConstants.SQL_FORMAT_KEYWORD_CASE_AUTO) ||
            store.contains(SQLPreferenceConstants.SQL_FORMAT_EXTRACT_FROM_SOURCE) ||

            store.contains(DBeaverPreferences.SCRIPT_TITLE_PATTERN) ||
            store.contains(DBeaverPreferences.SCRIPT_AUTO_FOLDERS)
        ;
    }

    @Override
    protected boolean supportsDataSourceSpecificOptions()
    {
        return true;
    }

    @Override
    protected Control createPreferenceContent(Composite parent)
    {
        Composite composite = UIUtils.createPlaceholder(parent, 1);

        Composite composite2 = UIUtils.createPlaceholder(composite, 2);
        ((GridLayout)composite2.getLayout()).horizontalSpacing = 5;
        composite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        {
            Group connectionsGroup = UIUtils.createControlGroup(composite2, "Connections", 1, GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL, 0);
            editorSeparateConnectionCheck = UIUtils.createCheckbox(connectionsGroup, "Open separate connection for each editor", false);

            connectOnActivationCheck = UIUtils.createCheckbox(connectionsGroup, "Connect on editor activation", false);
            connectOnExecuteCheck = UIUtils.createCheckbox(connectionsGroup, "Connect on query execute", false);
        }

        {
            Group connectionsGroup = UIUtils.createControlGroup(composite2, "Auto-save", 1, GridData.FILL_HORIZONTAL, 0);
            autoSaveOnClose = UIUtils.createCheckbox(connectionsGroup, "Auto-save editor on close", false);
            saveOnQueryExecution = UIUtils.createCheckbox(connectionsGroup, "Save editor on query execute", false);
        }

        // Autoclose
        {
            Composite acGroup = UIUtils.createControlGroup(composite2, "Auto close", 1, GridData.FILL_BOTH, 0);

            acSingleQuotesCheck = UIUtils.createCheckbox(acGroup, "Single quotes", false);
            acDoubleQuotesCheck = UIUtils.createCheckbox(acGroup, "Double quotes", false);
            acBracketsCheck = UIUtils.createCheckbox(acGroup, "Brackets", false);
        }

        {
            // Formatting
            Composite afGroup = UIUtils.createControlGroup(composite2, "Auto format", 1, GridData.FILL_BOTH, 0);
            afKeywordCase = UIUtils.createCheckbox(
                afGroup,
                "Convert keyword case",
                "Auto-convert keywords to upper/lower case on enter",
                false, 1);
            afExtractFromSource = UIUtils.createCheckbox(
                afGroup,
                "Extract SQL from source code",
                "On source code paste will remove all source language elements like quotes, +, \\n, etc", false, 1);
        }

        // Scripts
        {
            Composite scriptsGroup = UIUtils.createControlGroup(composite2, CoreMessages.pref_page_sql_editor_group_resources, 2, GridData.FILL_BOTH, 0);
            ((GridData)scriptsGroup.getLayoutData()).horizontalSpan = 2;

            autoFoldersCheck = UIUtils.createCheckbox(scriptsGroup, CoreMessages.pref_page_sql_editor_checkbox_put_new_scripts, null, false, 2);
            scriptTitlePattern = UIUtils.createLabelText(scriptsGroup, CoreMessages.pref_page_sql_editor_title_pattern, "");

            String[] vars = new String[] {SQLEditor.VAR_CONNECTION_NAME, SQLEditor.VAR_DRIVER_NAME, SQLEditor.VAR_FILE_NAME, SQLEditor.VAR_FILE_EXT};
            String[] explain = new String[] {"Connection name", "Database driver name", "File name", "File extension"};
            StringBuilder legend = new StringBuilder("Supported variables: ");
            for (int i = 0; i <vars.length; i++) {
                legend.append("\n\t- ${").append(vars[i]).append("}:  ").append(explain[i]);
            }
            scriptTitlePattern.setToolTipText(legend.toString());
        }

        return composite;
    }

    @Override
    protected void loadPreferences(DBPPreferenceStore store)
    {
        try {
            editorSeparateConnectionCheck.setSelection(store.getBoolean(DBeaverPreferences.EDITOR_SEPARATE_CONNECTION));
            connectOnActivationCheck.setSelection(store.getBoolean(DBeaverPreferences.EDITOR_CONNECT_ON_ACTIVATE));
            connectOnExecuteCheck.setSelection(store.getBoolean(DBeaverPreferences.EDITOR_CONNECT_ON_EXECUTE));

            autoSaveOnClose.setSelection(store.getBoolean(SQLPreferenceConstants.AUTO_SAVE_ON_CLOSE));
            saveOnQueryExecution.setSelection(store.getBoolean(SQLPreferenceConstants.AUTO_SAVE_ON_EXECUTE));

            acSingleQuotesCheck.setSelection(store.getBoolean(SQLPreferenceConstants.SQLEDITOR_CLOSE_SINGLE_QUOTES));
            acDoubleQuotesCheck.setSelection(store.getBoolean(SQLPreferenceConstants.SQLEDITOR_CLOSE_DOUBLE_QUOTES));
            acBracketsCheck.setSelection(store.getBoolean(SQLPreferenceConstants.SQLEDITOR_CLOSE_BRACKETS));
            afKeywordCase.setSelection(store.getBoolean(SQLPreferenceConstants.SQL_FORMAT_KEYWORD_CASE_AUTO));
            afExtractFromSource.setSelection(store.getBoolean(SQLPreferenceConstants.SQL_FORMAT_EXTRACT_FROM_SOURCE));

            autoFoldersCheck.setSelection(store.getBoolean(DBeaverPreferences.SCRIPT_AUTO_FOLDERS));
            scriptTitlePattern.setText(store.getString(DBeaverPreferences.SCRIPT_TITLE_PATTERN));

        } catch (Exception e) {
            log.warn(e);
        }
    }

    @Override
    protected void savePreferences(DBPPreferenceStore store)
    {
        try {
            store.setValue(DBeaverPreferences.EDITOR_SEPARATE_CONNECTION, editorSeparateConnectionCheck.getSelection());
            store.setValue(DBeaverPreferences.EDITOR_CONNECT_ON_ACTIVATE, connectOnActivationCheck.getSelection());
            store.setValue(DBeaverPreferences.EDITOR_CONNECT_ON_EXECUTE, connectOnExecuteCheck.getSelection());

            store.setValue(SQLPreferenceConstants.AUTO_SAVE_ON_CLOSE, autoSaveOnClose.getSelection());
            store.setValue(SQLPreferenceConstants.AUTO_SAVE_ON_EXECUTE, saveOnQueryExecution.getSelection());

            store.setValue(SQLPreferenceConstants.SQLEDITOR_CLOSE_SINGLE_QUOTES, acSingleQuotesCheck.getSelection());
            store.setValue(SQLPreferenceConstants.SQLEDITOR_CLOSE_DOUBLE_QUOTES, acDoubleQuotesCheck.getSelection());
            store.setValue(SQLPreferenceConstants.SQLEDITOR_CLOSE_BRACKETS, acBracketsCheck.getSelection());

            store.setValue(SQLPreferenceConstants.SQL_FORMAT_KEYWORD_CASE_AUTO, afKeywordCase.getSelection());
            store.setValue(SQLPreferenceConstants.SQL_FORMAT_EXTRACT_FROM_SOURCE, afExtractFromSource.getSelection());

            store.setValue(DBeaverPreferences.SCRIPT_AUTO_FOLDERS, autoFoldersCheck.getSelection());
            store.setValue(DBeaverPreferences.SCRIPT_TITLE_PATTERN, scriptTitlePattern.getText());
        } catch (Exception e) {
            log.warn(e);
        }
        PrefUtils.savePreferenceStore(store);
    }

    @Override
    protected void clearPreferences(DBPPreferenceStore store)
    {
        store.setToDefault(DBeaverPreferences.EDITOR_SEPARATE_CONNECTION);
        store.setToDefault(DBeaverPreferences.EDITOR_CONNECT_ON_ACTIVATE);
        store.setToDefault(DBeaverPreferences.EDITOR_CONNECT_ON_EXECUTE);

        store.setToDefault(SQLPreferenceConstants.AUTO_SAVE_ON_CLOSE);
        store.setToDefault(SQLPreferenceConstants.AUTO_SAVE_ON_EXECUTE);

        store.setToDefault(SQLPreferenceConstants.SQLEDITOR_CLOSE_SINGLE_QUOTES);
        store.setToDefault(SQLPreferenceConstants.SQLEDITOR_CLOSE_DOUBLE_QUOTES);
        store.setToDefault(SQLPreferenceConstants.SQLEDITOR_CLOSE_BRACKETS);
        store.setToDefault(SQLPreferenceConstants.SQL_FORMAT_KEYWORD_CASE_AUTO);
        store.setToDefault(SQLPreferenceConstants.SQL_FORMAT_EXTRACT_FROM_SOURCE);

        store.setToDefault(DBeaverPreferences.SCRIPT_AUTO_FOLDERS);
        store.setToDefault(DBeaverPreferences.SCRIPT_TITLE_PATTERN);
    }

    @Override
    protected String getPropertyPageID()
    {
        return PAGE_ID;
    }

}