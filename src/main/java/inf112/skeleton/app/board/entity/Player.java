package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.CardHandPacket;
import inf112.skeleton.common.packet.data.CardPacket;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;

public class Player {
    private final String uuid;
    public String name;
    Robot robot = null;
    Vector2 initialPos;
    int initialHp;
    Direction initialDirection;
    public Card[] cards;
    public Card[] selectedCards;
    public boolean[] cardPlayedByServer;
    int slot;

    /**
     * Player has its own class, which owns a robot, to avoid rendring on socket thread.
     *
     * @param uuid      Unique id of owner
     * @param name
     * @param pos
     * @param hp
     * @param slot
     * @param direction
     */
    public Player(String uuid, String name, Vector2 pos, int hp, int slot, Direction direction) {
        this.uuid = uuid;
        this.name = name;
        this.initialHp = hp;
        this.slot = slot;
        this.initialPos = pos;
        this.initialDirection = direction;
        this.cards = new Card[hp];
        this.selectedCards = new Card[5];
        this.cardPlayedByServer = new boolean[5];
    }

    /**
     * If robot is not yet created for player it should create it.
     */
    public void update() {
        if (robot == null) {
            this.robot = new Robot(initialPos.x, initialPos.y, slot, this);
            RoboRally.gameBoard.addEntity(robot);
        }
    }

    /**
     * Updates the array that tells the client if a card has been played by the server.
     *
     * @param packet containing a card that has been played.
     */
    public void receiveCardPacket(CardPacket packet) {
        Card foo = Tools.CARD_RECONSTRUCTOR.reconstructCard(packet.getPriority());
        for (int i = 0; i < selectedCards.length; i++) {
            if (selectedCards[i].equals(foo)) {
                cardPlayedByServer[i] = true;
                Gdx.app.log("Player - receiveCardPacket", "Set bool-arr pos " + i + " to true.");
                return;
            }
        }
    }

    /**
     * Receive a fresh hand from the server. Overwrites anything the player had before.
     *
     * @param packet An array of cards.
     */
    public void receiveCardHandPacket(CardHandPacket packet) {
        selectedCards = new Card[5];
        cardPlayedByServer = new boolean[5];
        int[] packetCardHand = packet.getHand();
        if (packetCardHand.length != cards.length) {
            cards = new Card[packetCardHand.length];
        }
        for (int i = 0; i < packetCardHand.length; i++) {
            cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(packetCardHand[i]);
        }
        selectedCards = new Card[5];

        while (true) {
            try {
                RoboRally.gameBoard.hud.getPlayerDeck().resetDeck();
                RoboRally.gameBoard.hud.turnTimer.start();
                return;
            } catch (NullPointerException npe) {
            }
        }
    }

    /**
     * Send one card to server where it will be used as a burnt card.
     * Call this when players hitpoints are below 5.
     */
    public void sendBurntCardToServer() {
        for (int i = 0; i < selectedCards.length; i++) {
            if (selectedCards[i] != null) {
                CardPacket data = new CardPacket(selectedCards[i]);
                new Packet(ToServer.CARD_PACKET.ordinal(), data).sendPacket(RoboRally.channel);
                Gdx.app.log("Player clientside - sendBurntCardToServer", "Constructed cardpacket: " + Tools.CARD_RECONSTRUCTOR.reconstructCard(data.getPriority()).toString());
                selectedCards[i] = null;

                return;
            }
        }
        Gdx.app.log("Player clientside - sendBurntCardToServer", "No cards selected");

    }


    /**
     * Accept packet related to any changes to this player, checks if its needed then applies changes.
     *
     * @param update UpdatePlayerPacket
     */
    public void updateRobot(UpdatePlayerPacket update) {
        robot.updateMovement(update);
        robot.updateHealth(update);
        RoboRally.gameBoard.hud.getPlayerDeck().setFromDeckHidden(true);
        RoboRally.gameBoard.hud.turnTimer.reset();
    }

    /**
     * Get the unique id of the player
     * @return unique id
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Get the robot owned by the player
     * @return Robot
     */
    public Robot getRobot() {
        return this.robot;
    }
}
