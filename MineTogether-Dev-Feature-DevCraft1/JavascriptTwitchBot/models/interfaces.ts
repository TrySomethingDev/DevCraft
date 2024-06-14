export interface CommandArgs {
    playerName: string;
    minecraftSkin: string;
    blocksToMine: number;
}

export interface StreamElementsPoints {
    points: number;
}

export interface MineTogetherConfig {
    General: {
        RequestedAction: string;
    };
    ChattersThatWantToPlay: string[];
}

export interface TwitchConfig {
    identity: {
        username: string;
        password: string;
    };
    channels: string[];
}

export interface BotConfig {
    twitch: TwitchConfig;
    streamElements: StreamElementsConfig;
    adminName: string;
    configFilePath: string;
}

interface StreamElementsConfig {
    token: string;
    channelId: string;
}
