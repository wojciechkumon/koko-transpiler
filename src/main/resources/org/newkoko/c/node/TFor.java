/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.newkoko.c.node;

import org.newkoko.c.analysis.*;

@SuppressWarnings("nls")
public final class TFor extends Token
{
    public TFor()
    {
        super.setText("for");
    }

    public TFor(int line, int pos)
    {
        super.setText("for");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TFor(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTFor(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TFor text.");
    }
}
