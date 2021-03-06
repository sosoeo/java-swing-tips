package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public final class MainPanel extends JPanel {
    private MainPanel() {
        super(new BorderLayout());

        JTree tree = new JTree(makeModel());
        tree.setRootVisible(false);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            private boolean isAdjusting;
            @Override public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
                // collapseAll(tree); // StackOverflowError when collapsing nodes below 2nd level
                if (isAdjusting) {
                    return;
                }
                isAdjusting = true;
                collapseFirstHierarchy(tree);
                tree.setSelectionPath(e.getPath());
                isAdjusting = false;
            }
            @Override public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
                // throw new ExpandVetoException(e, "Tree collapse cancelled");
            }
        });

        JScrollPane scroll = new JScrollPane(tree) {
            @Override public void updateUI() {
                setViewportBorder(null);
                super.updateUI();
                EventQueue.invokeLater(() -> setViewportBorder(BorderFactory.createLineBorder(getViewport().getView().getBackground(), 5)));
            }
        };
        add(scroll);
        setPreferredSize(new Dimension(320, 240));
    }
    private static DefaultTreeModel makeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode set1 = new DefaultMutableTreeNode("Set 001");
        DefaultMutableTreeNode set2 = new DefaultMutableTreeNode("Set 002");
        DefaultMutableTreeNode set3 = new DefaultMutableTreeNode("Set 003");
        DefaultMutableTreeNode set4 = new DefaultMutableTreeNode("Set 004");
        set1.add(new DefaultMutableTreeNode("111111111"));
        set1.add(new DefaultMutableTreeNode("22222222222"));
        set1.add(new DefaultMutableTreeNode("33333"));
        set2.add(new DefaultMutableTreeNode("asdfasdfas"));
        set2.add(new DefaultMutableTreeNode("asdf"));
        set3.add(new DefaultMutableTreeNode("asdfasdfasdf"));
        set3.add(new DefaultMutableTreeNode("qwerqwer"));
        set3.add(new DefaultMutableTreeNode("zvxcvzxcvzxzxcvzxcv"));
        set4.add(new DefaultMutableTreeNode("444"));
        root.add(set1);
        root.add(set2);
        set2.add(set3);
        root.add(set4);
        return new DefaultTreeModel(root);
    }
    public static void collapseFirstHierarchy(JTree tree) {
        TreeModel model = tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        // Java 9: Enumeration<TreeNode> e = root.breadthFirstEnumeration();
        Enumeration<?> e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            boolean isOverFirstLevel = node.getLevel() > 1;
            if (isOverFirstLevel) { // Collapse only nodes in the first hierarchy
                return;
            } else if (node.isLeaf() || node.isRoot()) {
                continue;
            }
            tree.collapsePath(new TreePath(node.getPath()));
        }
    }
    // private static void collapseAll(JTree tree) {
    //     int row = tree.getRowCount() - 1;
    //     while (row >= 0) { // The root node must be hidden
    //         tree.collapseRow(row--);
    //     }
    // }
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
