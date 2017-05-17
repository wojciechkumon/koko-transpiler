/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.koko.analysis;

import java.util.*;
import org.koko.node.*;

public class ReversedDepthFirstAdapter extends AnalysisAdapter
{
    public void inStart(Start node)
    {
        defaultIn(node);
    }

    public void outStart(Start node)
    {
        defaultOut(node);
    }

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    @Override
    public void caseStart(Start node)
    {
        inStart(node);
        node.getEOF().apply(this);
        node.getPProgram().apply(this);
        outStart(node);
    }

    public void inAProgram(AProgram node)
    {
        defaultIn(node);
    }

    public void outAProgram(AProgram node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAProgram(AProgram node)
    {
        inAProgram(node);
        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            Collections.reverse(copy);
            for(PExpr e : copy)
            {
                e.apply(this);
            }
        }
        outAProgram(node);
    }

    public void inABindExpr(ABindExpr node)
    {
        defaultIn(node);
    }

    public void outABindExpr(ABindExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseABindExpr(ABindExpr node)
    {
        inABindExpr(node);
        if(node.getBindExpr() != null)
        {
            node.getBindExpr().apply(this);
        }
        outABindExpr(node);
    }

    public void inAPureExpr(APureExpr node)
    {
        defaultIn(node);
    }

    public void outAPureExpr(APureExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPureExpr(APureExpr node)
    {
        inAPureExpr(node);
        if(node.getPureExpr() != null)
        {
            node.getPureExpr().apply(this);
        }
        outAPureExpr(node);
    }

    public void inAFundefBindExpr(AFundefBindExpr node)
    {
        defaultIn(node);
    }

    public void outAFundefBindExpr(AFundefBindExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFundefBindExpr(AFundefBindExpr node)
    {
        inAFundefBindExpr(node);
        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getFbody());
            Collections.reverse(copy);
            for(PExpr e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PFunArg> copy = new ArrayList<PFunArg>(node.getFargstypes());
            Collections.reverse(copy);
            for(PFunArg e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getFname() != null)
        {
            node.getFname().apply(this);
        }
        if(node.getFtype() != null)
        {
            node.getFtype().apply(this);
        }
        outAFundefBindExpr(node);
    }

    public void inACmpPureExpr(ACmpPureExpr node)
    {
        defaultIn(node);
    }

    public void outACmpPureExpr(ACmpPureExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACmpPureExpr(ACmpPureExpr node)
    {
        inACmpPureExpr(node);
        {
            List<PPureExpr> copy = new ArrayList<PPureExpr>(node.getArgs());
            Collections.reverse(copy);
            for(PPureExpr e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getOp() != null)
        {
            node.getOp().apply(this);
        }
        outACmpPureExpr(node);
    }

    public void inACondPureExpr(ACondPureExpr node)
    {
        defaultIn(node);
    }

    public void outACondPureExpr(ACondPureExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACondPureExpr(ACondPureExpr node)
    {
        inACondPureExpr(node);
        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getIffalse());
            Collections.reverse(copy);
            for(PExpr e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getIftrue());
            Collections.reverse(copy);
            for(PExpr e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getPred() != null)
        {
            node.getPred().apply(this);
        }
        outACondPureExpr(node);
    }

    public void inANumberPureExpr(ANumberPureExpr node)
    {
        defaultIn(node);
    }

    public void outANumberPureExpr(ANumberPureExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANumberPureExpr(ANumberPureExpr node)
    {
        inANumberPureExpr(node);
        if(node.getNumber() != null)
        {
            node.getNumber().apply(this);
        }
        outANumberPureExpr(node);
    }

    public void inAVarPureExpr(AVarPureExpr node)
    {
        defaultIn(node);
    }

    public void outAVarPureExpr(AVarPureExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAVarPureExpr(AVarPureExpr node)
    {
        inAVarPureExpr(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAVarPureExpr(node);
    }

    public void inAArgtupFunArg(AArgtupFunArg node)
    {
        defaultIn(node);
    }

    public void outAArgtupFunArg(AArgtupFunArg node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAArgtupFunArg(AArgtupFunArg node)
    {
        inAArgtupFunArg(node);
        if(node.getArgVar() != null)
        {
            node.getArgVar().apply(this);
        }
        if(node.getArgType() != null)
        {
            node.getArgType().apply(this);
        }
        outAArgtupFunArg(node);
    }
}
