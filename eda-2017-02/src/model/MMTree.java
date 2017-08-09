package model;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MMTree implements ModelConstants {

    private class Node {
        private Board board;

        Node(Board board) {
            this.board = board;
        }
    }

    public MMTree() {
    }

    private Board bestBoard = null;

    public Board minimaxWithDepth(Board board, int playerToPlay, int depth, boolean pruneActivated, boolean treeActivated) {

        long i = System.currentTimeMillis();

        HashSet<Board> path = new HashSet<>();

        DotNode root = new DotNode("a");
        root.maximizing = true;

        if (pruneActivated) {
            minimaxGoWithPrune(new Node(board), playerToPlay, depth, new HeuristicGo(), BoardManager.getCurrentBoardHashcode(), true, path, true, LOWEST_RATING, HIGHEST_RATING, root);
        } else {
            minimaxGo(new Node(board), playerToPlay, depth, new HeuristicGo(), BoardManager.getCurrentBoardHashcode(), true, path, true, root);
        }

        System.out.println(System.currentTimeMillis() - i + " miliseconds.");

        PrintWriter pw = null;
        if (treeActivated) {
            File file = new File("../../docs/tree.dot");
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                pw = new PrintWriter(file);
                pw.println("digraph mmtree {");
                pw.println("a" + " [color=red,label=\"START " + playerToPlay + "\"];");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


        if (treeActivated) {
            printTree(root, pw);
            if (pw != null) {
                pw.println("}");
                pw.close();
            }
        }

        return bestBoard;
    }

    public Board minimaxWithMaxTime(Board board, int playerToPlay, int maxTime, boolean pruneActivated, boolean treeActivated) {

        TimeLimiter limiter = new SimpleTimeLimiter();

        try {
            limiter.callWithTimeout(new Callable<Board>() {
                @Override
                public Board call() throws Exception {

                    return minimaxWithDepth(board, playerToPlay, MAXTIME_DEPTH, pruneActivated, treeActivated);
                }
            }, maxTime, TimeUnit.SECONDS, false);
        } catch (UncheckedTimeoutException e) {

            if (bestBoard == null) { //This only happens it time limit cuts minimax before first branch finished processing.
                Iterator<Board> itr = BoardManager.getIterator(board, getPlayerMM(true, playerToPlay), board.hashCode(),
                        BoardManager.getPreviousBoardHashcode());
                bestBoard = itr.next();

            }

            return bestBoard;
        } catch (Exception e) {
            return null;
        }

        return bestBoard;

    }

    private int minimaxGo(Node node, int player, int depth, Heuristic moneyball, int previousHash, boolean maximizing, HashSet<Board> path, boolean first_pass, DotNode parent) {
        if (depth == 0) {

            int heuristic = moneyball.value(node.board);

            if (player == WHITE)
                return heuristic;
            else
                return -1 * heuristic;
        }


        Iterator<Board> itr = BoardManager.getIterator(node.board, getPlayerMM(maximizing, player), node.board.hashCode(),
                previousHash);


        if (!itr.hasNext()) {
            int heuristic = moneyball.value(node.board);

            if (player == WHITE)
                return heuristic;
            else
                return -1 * heuristic;
        }

        path.add(node.board);

        if (maximizing) {
            //Max level

            int maxValue = LOWEST_RATING;
            DotNode maxDotNode = null;

            while (itr.hasNext()) {

                Node n = new Node(itr.next());
                if (!path.contains(n.board)) {

                    DotNode aux = new DotNode(n.board.getPreviousPlay());
                    aux.maximizing = true;

                    parent.addChild(aux);

                    int h = minimaxGo(n, player, depth - 1, moneyball, node.board.hashCode(), false, path, false, aux);

                    aux.setValue(h);

                    if (h > maxValue) {
                        maxValue = h;
                        maxDotNode = aux;
                        if (first_pass) // This is so that only the board from the next move (from current board) is saved. This cannot happen in minimizing because minimax is allways called with maximizing true.
                            bestBoard = n.board;

                    }

                }
            }

            if (maxDotNode != null) maxDotNode.color = true;

            path.remove(node.board);

            return maxValue;

        } else {
            //Min level

            int minValue = HIGHEST_RATING;
            DotNode minDotNode = null;

            while (itr.hasNext()) {

                Node n = new Node(itr.next());
                if (!path.contains(n.board)) {

                    DotNode aux = new DotNode(n.board.getPreviousPlay());
                    aux.maximizing = false;

                    parent.addChild(aux);

                    int h = minimaxGo(n, player, depth - 1, moneyball, node.board.hashCode(), true, path, false, aux);

                    aux.setValue(h);

                    if (h < minValue) {
                        minValue = h;
                        minDotNode = aux;
                    }
                }
            }

            if (minDotNode != null) minDotNode.color = true;

            path.remove(node.board);

            return minValue;
        }
    }

    private int minimaxGoWithPrune(Node node, int player, int depth, Heuristic moneyball, int previousHash, boolean maximizing, HashSet<Board> path, boolean first_pass, int alpha, int beta, DotNode parent) {
        if (depth == 0) {
            int heuristic = moneyball.value(node.board);

            if (player == WHITE)
                return heuristic;
            else
                return -1 * heuristic;
        }


        Iterator<Board> itr = BoardManager.getIterator(node.board, getPlayerMM(maximizing, player), node.board.hashCode(),
                previousHash);

        if (!itr.hasNext()) {
            int heuristic = moneyball.value(node.board);

            if (player == WHITE)
                return heuristic;
            else
                return -1 * heuristic;
        }

        path.add(node.board);

        if (maximizing) {
            //Max level

            int maxValue = LOWEST_RATING;
            DotNode maxDotNode = null;

            while (itr.hasNext()) {

                Node n = new Node(itr.next());

                if (!path.contains(n.board)) {

                    DotNode aux = new DotNode(n.board.getPreviousPlay());
                    aux.setMaximizing(true);

                    parent.addChild(aux);

                    int h = minimaxGoWithPrune(n, player, depth - 1, moneyball, node.board.hashCode(), false, path, false, alpha, beta, aux);

                    aux.setValue(h);

                    if (h > maxValue) {
                        maxValue = h;
                        maxDotNode = aux;
                        if (first_pass) // This is so that only the board from the next move (from current board) is saved. This cannot happen in minimizing because minimax is allways called with maximizing true.
                            bestBoard = n.board;
                    }

                    if (h > alpha) {
                        alpha = h;
                    }

                    // Alpha Beta Pruning
                    if (beta <= alpha) {
                        aux.setPrune(true);
                        break;
                    }

                }


            }

            if (maxDotNode != null) maxDotNode.color = true;

            path.remove(node.board);

            return maxValue;

        } else {
            //Min level

            int minValue = HIGHEST_RATING;
            DotNode minDotNode = null;

            while (itr.hasNext()) {

                Node n = new Node(itr.next());

                if (!path.contains(n.board)) {

                    DotNode aux = new DotNode(n.board.getPreviousPlay());
                    aux.setMaximizing(false);

                    parent.addChild(aux);

                    int h = minimaxGoWithPrune(n, player, depth - 1, moneyball, node.board.hashCode(), true, path, false, alpha, beta, aux);

                    aux.setValue(h);

                    if (h < minValue) {
                        minValue = h;
                        minDotNode = aux;
                    }

                    if (h < beta) {
                        beta = h;
                    }

                    // Alpha Beta Pruning
                    if (beta <= alpha) {
                        aux.setPrune(true);
                        break;
                    }
                }


            }

            if (minDotNode != null) minDotNode.color = true;

            path.remove(node.board);

            return minValue;
        }
    }

    private int getPlayerMM(boolean maximizing, int player) {
        if (maximizing)
            return player;
        else
            return otherPlayer(player);
    }

    private static int otherPlayer(int currentPlayer) {
        return currentPlayer == BLACK ? WHITE : BLACK;
    }

    class DotNode {

        Point point;
        int value;
        String text;
        List<DotNode> children = new LinkedList<>();
        boolean maximizing;
        boolean color = false;
        boolean isPrune;

        DotNode(Point point) {
            this.point = point;
        }

        DotNode(String text) {
            this.text = text;
        }

        @Override
        public String toString() {

            if (text != null) {
                return text;
            }

            return hashCode() + " [label=\"(" + point.x + "," + point.y + ") " + value + "\"]";
        }

        void setValue(int value) {
            this.value = value;
        }

        void addChild(DotNode aux) {
            children.add(aux);
        }

        void setPrune(boolean prune) {
            isPrune = prune;
        }

        void setMaximizing(boolean maximizing) {
            this.maximizing = maximizing;
        }
    }

    private void printTree(DotNode current, PrintWriter pw) {

        if (current == null) return;

        for (DotNode dn : current.children) {

            String s = dn.toString();

            if (dn.color && !dn.isPrune)
                s += "[color=red]";
            if (!dn.maximizing)
                s += "[shape=box]";
            if (dn.isPrune)
                s += "[style=filled,fillcolor=grey]";


            pw.println(s);

            if (current.text != null) {
                pw.println(current.toString() + " -> " + dn.hashCode());
            } else {
                pw.println(current.hashCode() + " -> " + dn.hashCode());
            }

            printTree(dn, pw);

        }

    }

}
