/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.koko.node;

import org.koko.analysis.*;

@SuppressWarnings("nls")
public final class TNumber extends Token
{
    public TNumber(String text)
    {
        setText(text);
    }

    public TNumber(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TNumber(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTNumber(this);
    }
}
