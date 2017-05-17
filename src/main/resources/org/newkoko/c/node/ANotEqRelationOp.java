/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.newkoko.c.node;

import org.newkoko.c.analysis.*;

@SuppressWarnings("nls")
public final class ANotEqRelationOp extends PRelationOp
{
    private TNotEq _notEq_;

    public ANotEqRelationOp()
    {
        // Constructor
    }

    public ANotEqRelationOp(
        @SuppressWarnings("hiding") TNotEq _notEq_)
    {
        // Constructor
        setNotEq(_notEq_);

    }

    @Override
    public Object clone()
    {
        return new ANotEqRelationOp(
            cloneNode(this._notEq_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANotEqRelationOp(this);
    }

    public TNotEq getNotEq()
    {
        return this._notEq_;
    }

    public void setNotEq(TNotEq node)
    {
        if(this._notEq_ != null)
        {
            this._notEq_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._notEq_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._notEq_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._notEq_ == child)
        {
            this._notEq_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._notEq_ == oldChild)
        {
            setNotEq((TNotEq) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
