/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.newkoko.c.node;

import org.newkoko.c.analysis.*;

@SuppressWarnings("nls")
public final class ACallUnaryExpression extends PUnaryExpression
{
    private PCallExpression _callExpression_;

    public ACallUnaryExpression()
    {
        // Constructor
    }

    public ACallUnaryExpression(
        @SuppressWarnings("hiding") PCallExpression _callExpression_)
    {
        // Constructor
        setCallExpression(_callExpression_);

    }

    @Override
    public Object clone()
    {
        return new ACallUnaryExpression(
            cloneNode(this._callExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACallUnaryExpression(this);
    }

    public PCallExpression getCallExpression()
    {
        return this._callExpression_;
    }

    public void setCallExpression(PCallExpression node)
    {
        if(this._callExpression_ != null)
        {
            this._callExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._callExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._callExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._callExpression_ == child)
        {
            this._callExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._callExpression_ == oldChild)
        {
            setCallExpression((PCallExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
