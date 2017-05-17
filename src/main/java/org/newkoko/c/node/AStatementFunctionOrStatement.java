/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.newkoko.c.node;

import org.newkoko.c.analysis.*;

@SuppressWarnings("nls")
public final class AStatementFunctionOrStatement extends PFunctionOrStatement
{
    private PStatement _statement_;

    public AStatementFunctionOrStatement()
    {
        // Constructor
    }

    public AStatementFunctionOrStatement(
        @SuppressWarnings("hiding") PStatement _statement_)
    {
        // Constructor
        setStatement(_statement_);

    }

    @Override
    public Object clone()
    {
        return new AStatementFunctionOrStatement(
            cloneNode(this._statement_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStatementFunctionOrStatement(this);
    }

    public PStatement getStatement()
    {
        return this._statement_;
    }

    public void setStatement(PStatement node)
    {
        if(this._statement_ != null)
        {
            this._statement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._statement_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._statement_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._statement_ == child)
        {
            this._statement_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._statement_ == oldChild)
        {
            setStatement((PStatement) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
