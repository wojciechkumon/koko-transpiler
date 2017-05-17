package calc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import expression.analysis.DepthFirstAdapter;
import expression.node.EOF;
import expression.node.Node;
import expression.node.Start;
import expression.node.Token;

public class ASTDisplay extends DepthFirstAdapter {
  private Stack<DefaultMutableTreeNode> parents = new Stack<>();

  @Override
  public void outStart(Start node) {
    JFrame frame = new JFrame("AST Displayer");
    JTree tree = new JTree(parents.pop());
    JScrollPane pane = new JScrollPane(tree);

    expandAll(tree);

    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    frame.setSize(300, 400);
    frame.getContentPane().add(pane);
    frame.setVisible(true);
  }

  /*
   * As we come across non terminals, push them onto the stack
   */
  public void defaultIn(Node node) {
    DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode
        (node.getClass().getName().substring
            (node.getClass().getName().lastIndexOf('.') + 1));
    parents.push(thisNode);
  }

  /*
   * As we leave a non terminal, it's parent is the node before it
   * on the stack, so we pop it off and add it to that node
   */
  public void defaultOut(Node node) {
    DefaultMutableTreeNode thisNode = parents.pop();
    (parents.peek()).add(thisNode);
  }

  /*
   * Terminals - our parent is always on the top of the stack, so we
   * add ourselves to it
   */
  public void defaultCase(Node node) {
    DefaultMutableTreeNode thisNode = new
        DefaultMutableTreeNode(((Token) node).getText());
    (parents.peek()).add(thisNode);
  }

  public void caseEOF(EOF node) {
  }

  /*
   * we want to see the whole tree. These functions expand it for
   * us, they are written by Christian Kaufhold and taken from the
   * comp.lang.jave.help newsgroup
   */
  private static void expandAll(JTree tree) {
    Object root = tree.getModel().getRoot();

    if (root != null)
      expandAll(tree, new TreePath(root));
  }


  private static void expandAll(JTree tree, TreePath path) {
    for (Object o : extremalPaths(tree.getModel(), path, new HashSet<>())) {
      tree.expandPath((TreePath) o);
    }
  }

  /**
   * The "extremal paths" of the tree model's subtree starting at
   * path. The extremal paths are those paths that a) are non-leaves
   * and b) have only leaf children, if any. It suffices to know
   * these to know all non-leaf paths in the subtree, and those are
   * the ones that matter for expansion (since the concept of expan-
   * sion only applies to non-leaves).
   * The extremal paths of a leaves is an empty collection.
   * This method uses the usual collection filling idiom, i.e.
   * clear and then fill the collection that it is given, and
   * for convenience return it. The extremal paths are stored
   * in the order in which they appear in pre-order in the
   * tree model.
   */
  private static Collection<TreePath> extremalPaths(TreeModel data,
                                                    TreePath path,
                                                    Collection<TreePath> result) {
    result.clear();

    if (data.isLeaf(path.getLastPathComponent())) {
      return result; // should really be forbidden (?)
    }

    extremalPathsImpl(data, path, result);

    return result;
  }

  private static void extremalPathsImpl(TreeModel data,
                                        TreePath path,
                                        Collection<TreePath> result) {
    Object node = path.getLastPathComponent();

    boolean hasNonLeafChildren = false;

    int count = data.getChildCount(node);

    for (int i = 0; i < count; i++)
      if (!data.isLeaf(data.getChild(node, i)))
        hasNonLeafChildren = true;

    if (!hasNonLeafChildren) {
      result.add(path);
    } else {
      for (int i = 0; i < count; i++) {
        Object child = data.getChild(node, i);

        if (!data.isLeaf(child))
          extremalPathsImpl(data,
              path.pathByAddingChild(child),
              result);
      }
    }
  }
}



