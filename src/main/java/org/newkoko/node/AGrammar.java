/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.newkoko.node;

import java.util.*;
import org.newkoko.analysis.*;

@SuppressWarnings("nls")
public final class AGrammar extends PGrammar
{
    private final LinkedList<PStatement> _statement_ = new LinkedList<PStatement>();

    public AGrammar()
    {
        // Constructor
    }

    public AGrammar(
        @SuppressWarnings("hiding") List<?> _statement_)
    {
        // Constructor
        setStatement(_statement_);

    }

    @Override
    public Object clone()
    {
        return new AGrammar(
            cloneList(this._statement_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAGrammar(this);
    }

    public LinkedList<PStatement> getStatement()
    {
        return this._statement_;
    }

    public void setStatement(List<?> list)
    {
        for(PStatement e : this._statement_)
        {
            e.parent(null);
        }
        this._statement_.clear();

        for(Object obj_e : list)
        {
            PStatement e = (PStatement) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._statement_.add(e);
        }
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
        if(this._statement_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PStatement> i = this._statement_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PStatement) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
