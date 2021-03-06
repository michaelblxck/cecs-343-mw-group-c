package com.groupc.android.illuminati.Objects;

import com.groupc.android.illuminati.MainScreen;
import com.groupc.android.illuminati.Objects.Table.AttackEnum;

public class Attack {
    private Player attackingPlayer;
    private Player defendingPlayer;
    private AttackEnum attackType;
    private Center center;
    private DestroyedCards destroyedCards;
    private NonSpecialCard attackingCard;
    private GroupCard defendingCard;
    private NonSpecialCard puppetCard;
    private boolean isSuccessful;
    private int sum;
    private int puppetArrow;
    public Table table;
    public AttackAnnouncement announcement;

    public Attack(Player attackingPlayer, NonSpecialCard attackingCard, Player defendingPlayer, GroupCard defendingCard, NonSpecialCard puppetCard, int puppetArrow, boolean isAttackingAPlayer, AttackEnum attackType)
    {
        this.attackingPlayer = attackingPlayer;
        this.attackingCard = attackingCard;
        this.defendingCard = defendingCard;
        this.puppetCard = puppetCard;
        this.puppetArrow = puppetArrow;
        this.attackType = attackType;
        table = MainScreen.getTable();
        center = table.getCenter();
    }

    public void setUpAttackAnnouncement() {
        announcement = new AttackAnnouncement(
                attackingPlayer,
                attackingCard,
                defendingPlayer,
                defendingCard);
        announcement.setAlignmentBonus(new AlignmentBonus(attackingCard, defendingCard).getAlignmentBonus());
        announcement.setPowerStructurePositionBonus(new PowerStructurePositionBonus(defendingPlayer, defendingCard).getPowerStructurePositionBonus());
        announcement.setSpecialPowerBonus(new SpecialPowerBonus(attackingCard, defendingCard, this).getSpecialPowerBonus());
        //announcement.setAttackerMoneyBonus();
        announcement.send(defendingPlayer);

        if (announcement.isAccepted()) {
            rollDice(announcement);
        } else endAttack();
    }

    private int rollDice(AttackAnnouncement announcement) {
        DiceRoll diceRoll = new DiceRoll();
        sum = diceRoll.getDiceSum();
        sum -= MainScreen.cheatInt;
        if (sum <= announcement.getScore() && sum < 11) attackIsSuccessful(puppetCard);
        return sum;
    }

    private void attackIsSuccessful(NonSpecialCard puppet) {
        isSuccessful = true;
        if (defendingCard.getCardName().equals("Survivalists")) {
            if (defendingPlayer != null) defendingPlayer.setOwnsSurvivalists(false);
            attackingPlayer.setOwnsSurvivalists(true);
        }

        if (attackType == AttackEnum.CONTROL) {
            if (attackingPlayer.getPowerStructure().hasRoom(defendingCard)) {
                attackingPlayer.getPowerStructure().addToPowerStructure(puppet, defendingCard, puppetArrow);
                center.removeGroupFromCenter(defendingCard);
            } else {
                for (int i = 0; i < defendingCard.getConnectedCards().length; i++) {
                    if (defendingCard.getConnectedCards()[i] != null) {
                        GroupCard topCard = defendingCard.getConnectedCards()[i];
                        defendingCard.removePuppet(topCard);
                        center.addGroupToCenter(topCard);
                    }
                }
                if (attackingPlayer.getPowerStructure().hasRoom(defendingCard)) {
                    attackingPlayer.getPowerStructure().addToPowerStructure(puppet, defendingCard, puppetArrow);
                    center.removeGroupFromCenter(defendingCard);
                } else return;
            }
        }

        if (attackType == AttackEnum.NEUTRALIZE) {
            for (int i = 0; i < defendingCard.getConnectedCards().length; i++) {
                if (defendingCard.getConnectedCards()[i] != null) {
                    GroupCard topCard = defendingCard.getConnectedCards()[i];
                    defendingCard.removePuppet(topCard);
                    center.addGroupToCenter(topCard);
                }
            }
            center.addGroupToCenter(defendingCard);
        }

        if (attackType == AttackEnum.DESTROY) {
            for (int i = 0; i < defendingCard.getConnectedCards().length; i++) {
                if (defendingCard.getConnectedCards()[i] != null) {
                    GroupCard topCard = defendingCard.getConnectedCards()[i];
                    defendingCard.removePuppet(topCard);
                    center.addGroupToCenter(topCard);
                }
            }
            destroyedCards.addDestroyedCard(defendingCard);
        }
    }

    public void endAttack() {}

    public AttackEnum getAttackType() { return attackType; }

    public void setAttackType(AttackEnum attackType) { this.attackType = attackType; }

    public boolean isSuccessful() { return isSuccessful; }

    public AttackAnnouncement getAttackAnnouncement() { return announcement; }

    public int getAttackPower() { return attackingCard.getPower(); }

    public int getDefendingResistance() { return defendingCard.getResistance(); }

    public String getAttackName() { return attackingCard.getCardName(); }

    public String getDefendName() { return defendingCard.getCardName(); }

    public int getDiceSum() { return sum; }
}
