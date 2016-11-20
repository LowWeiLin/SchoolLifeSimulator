package com.officelife.ui;

import java.io.IOException;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

class GUI {

    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private Panel gamePanel;

    GUI(ComponentRenderer<Panel> gameRenderer) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        final Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        gamePanel = new Panel();

        panel.addComponent(gamePanel);

        window = new BasicWindow();
        window.setComponent(panel);

        gui = new MultiWindowTextGUI(screen,
            new DefaultWindowManager(),
            new EmptySpace(TextColor.ANSI.BLACK));

        gamePanel.setRenderer(gameRenderer);
    }

    void run() {
        gui.addWindowAndWait(window);
    }

    void update() {
        runLater(gamePanel::invalidate);
    }

    private void runLater(Runnable action) {
        try {
            gui.getGUIThread().invokeAndWait(action);
        } catch (InterruptedException e) {
            // TODO better logging
            System.err.println("Could not run action");
            e.printStackTrace();
        }
    }
}
