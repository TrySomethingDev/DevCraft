package net.trysomethingdev.devcraft.services;

public enum NPCMiningState {
    InitialTravelingToPlayer
    ,GivingItemFrameAndPick
    ,WaitingForItemFrameAndPickPlacement
    ,ApproachingMineEntrance
    ,Mining
    ,FinishedMining
    ,TravelingToPlayerToPlaceChest
    ,PlacingChest
    ,WaitingForPlayerToOpenAndCloseChest
    ,TakingChestAndWalkingAway
    ,StoreItemsAtMainBase
}
