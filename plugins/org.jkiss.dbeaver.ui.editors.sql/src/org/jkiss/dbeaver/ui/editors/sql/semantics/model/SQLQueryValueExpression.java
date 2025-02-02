/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ui.editors.sql.semantics.model;

import org.antlr.v4.runtime.misc.Interval;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.stm.STMTreeNode;
import org.jkiss.dbeaver.ui.editors.sql.semantics.SQLQueryRecognitionContext;
import org.jkiss.dbeaver.ui.editors.sql.semantics.SQLQuerySymbol;
import org.jkiss.dbeaver.ui.editors.sql.semantics.context.SQLQueryDataContext;
import org.jkiss.dbeaver.ui.editors.sql.semantics.context.SQLQueryExprType;
import org.jkiss.dbeaver.ui.editors.sql.semantics.context.SQLQueryResultTupleContext.SQLQueryResultColumn;

public abstract class SQLQueryValueExpression extends SQLQueryNodeModel {

    @NotNull
    protected SQLQueryExprType type = SQLQueryExprType.UNKNOWN;
    
    protected SQLQueryDataContext dataContext = null;

    public SQLQueryValueExpression(STMTreeNode syntaxNode, SQLQueryNodeModel ... subnodes) {
        this(syntaxNode.getRealInterval(), syntaxNode, subnodes);
    }

    public SQLQueryValueExpression(Interval region, STMTreeNode syntaxNode, SQLQueryNodeModel ... subnodes) {
        super(region, syntaxNode, subnodes);
    }
    
    public String getExprContent() {
        return this.getSyntaxNode().getTextContent();
    }
    
    @NotNull
    public final SQLQueryExprType getValueType() {
        return type;
    }

    @Nullable
    public SQLQuerySymbol getColumnNameIfTrivialExpression() {
        return null;
    }

    @Nullable
    public SQLQueryResultColumn getColumnIfTrivialExpression() {
        return null;
    }

    @Override
    public SQLQueryDataContext getGivenDataContext() {
        return this.dataContext;
    }
    
    @Override
    public SQLQueryDataContext getResultDataContext() {
        return this.dataContext;
    }

    public final void propagateContext(@NotNull SQLQueryDataContext context, @NotNull SQLQueryRecognitionContext statistics) {
        this.dataContext = context;
        this.propagateContextImpl(context, statistics);
    }
    
    protected abstract void propagateContextImpl(@NotNull SQLQueryDataContext context, @NotNull SQLQueryRecognitionContext statistics);
}

