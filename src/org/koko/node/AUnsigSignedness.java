/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.koko.node;

import org.koko.analysis.*;

@SuppressWarnings("nls")
public final class AUnsigSignedness extends PSignedness
{
    private TUnsigned _unsigned_;

    public AUnsigSignedness()
    {
        // Constructor
    }

    public AUnsigSignedness(
        @SuppressWarnings("hiding") TUnsigned _unsigned_)
    {
        // Constructor
        setUnsigned(_unsigned_);

    }

    @Override
    public Object clone()
    {
        return new AUnsigSignedness(
            cloneNode(this._unsigned_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUnsigSignedness(this);
    }

    public TUnsigned getUnsigned()
    {
        return this._unsigned_;
    }

    public void setUnsigned(TUnsigned node)
    {
        if(this._unsigned_ != null)
        {
            this._unsigned_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._unsigned_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._unsigned_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._unsigned_ == child)
        {
            this._unsigned_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._unsigned_ == oldChild)
        {
            setUnsigned((TUnsigned) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
