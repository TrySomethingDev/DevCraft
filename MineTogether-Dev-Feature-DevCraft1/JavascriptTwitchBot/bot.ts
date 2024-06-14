import * as tmi from "tmi.js";
import axios from "axios";
import * as fs from "node:fs";
import * as yaml from "js-yaml";
import {BotConfig, CommandArgs, MineTogetherConfig, StreamElementsPoints, TwitchConfig} from "./models/interfaces";
import dotenv from "dotenv";

const twitchConfig: TwitchConfig = {
    identity: {
        username: process.env.TWITCH_USERNAME!,
        password: process.env.TWITCH_PASSWORD!,
    },
    channels: [process.env.TWITCH_CHANNEL_NAME!],
};

const botConfig: BotConfig = {
    twitch: twitchConfig,
    streamElements: {
        token: process.env.STREAMELEMENTS_TOKEN!,
        channelId: process.env.STREAMELEMENTS_CHANNEL_ID!,
    },
    adminName: process.env.TWITCH_CHANNEL_NAME!,
    configFilePath: process.env.CONFIG_FILE_PATH!,
};
dotenv.config();
const client: tmi.Client = new tmi.client(twitchConfig);
let isMineTogetherModeActive: boolean = false;

client.on("message", onMessageHandler);
client.on("connected", (addr, port) => {
    console.log(`* Connected to ${addr}:${port}`);
});

async function onMessageHandler(target: string, context: tmi.ChatUserstate, msg: string, self: boolean) {
    if (self) return;
    const commandName = msg.trim().toUpperCase();
    await checkMineTogetherModeStatus(target);

    if (commandName === "!MINECLEAR" && context["display-name"] === botConfig.adminName) {
        await clearMinerList(target);
    } else if (commandName.startsWith("!MINE")) {
        await handleMineCommand(target, context, commandName);
    }
}

export const handleMineCommand = async (target: string, context: tmi.ChatUserstate, command: string) => {
    if (!isMineTogetherModeActive) return client.say(target, "Mine Together Mode is not active.");

    const {playerName, minecraftSkin, blocksToMine} = parseMineCommand(command, context);
    const points = await getStreamElementsPoints(playerName);
    const pointsToSpend = Math.min(points, blocksToMine);

    if (pointsToSpend > 0) {
        await Promise.all([
            changeStreamElementsPoints(target, playerName, -pointsToSpend),
            addMinerToConfig(context, pointsToSpend, minecraftSkin, target),
            client.say(target, `${context["display-name"]}, you've been added to the list with ${pointsToSpend} blocks.`)
        ]);
    } else {
        await client.say(target, `Sorry ${playerName}, you don't have enough points.`);
    }
};


export const parseMineCommand = (command: string, context: tmi.ChatUserstate): CommandArgs => {
    const [_, blocksArg, skinArg] = command.split(" ");
    const playerName = context["display-name"] ?? '';

    const blocksToMine = parseInt(blocksArg, 10) || 24;
    const minecraftSkin = isNaN(Number(blocksArg)) ? blocksArg : skinArg || "Player";

    return {playerName, minecraftSkin, blocksToMine};
};

export const getStreamElementsPoints = async (username: string): Promise<number> => {
    try {
        const response = await axios.get<StreamElementsPoints>(
            `https://api.streamelements.com/kappa/v2/points/${botConfig.streamElements.channelId}/${username}`
        );
        return response.data.points;
    } catch (error) {
        console.error("Error fetching StreamElements points:", error);
        throw error;
    }
};

export const changeStreamElementsPoints = async (target: string, username: string, points: number) => {
    const url = `https://api.streamelements.com/kappa/v2/points/${botConfig.streamElements.channelId}/${username}/${points}`;
    const headers = {
        Authorization: `Bearer ${botConfig.streamElements.token}`,
    };
    try {
        await axios.put(url, {}, {headers});
        await client.say(target, `Changed ${username}'s point balance by ${points} points.`);
    } catch (error) {
        console.error("Error changing StreamElements points:", error);
    }
};

export const addMinerToConfig = async (
    context: tmi.ChatUserstate,
    blocksToMine: number,
    minecraftSkin: string,
    target: string
) => {
    try {
        const config: MineTogetherConfig = yaml.load(fs.readFileSync(botConfig.configFilePath, "utf8")) as MineTogetherConfig;
        config.ChattersThatWantToPlay.push(`${context["display-name"]}, ${blocksToMine}, ${minecraftSkin}`);
        fs.writeFileSync(botConfig.configFilePath, yaml.dump(config));
        await client.say(target, `${context["display-name"]}, you've been added to the list.`);
    } catch (error) {
        console.error("Error adding miner to config:", error);
    }
};

export const checkMineTogetherModeStatus = async (target: string) => {
    try {
        const config = yaml.load(fs.readFileSync(botConfig.configFilePath, "utf8")) as MineTogetherConfig;
        if (!config?.General) new Error("Invalid MineTogetherMode config file.");

        if (config.General.RequestedAction === "StartRequested") {
            await startMineTogetherMode(target, config);
        }
    } catch (error: any) {
        console.error(error.message);
    }
};


export const startMineTogetherMode = async (target: string, config: MineTogetherConfig) => {
    await client.say(target, "Mine Together Mode activated!");
    isMineTogetherModeActive = true;
    await startingMessageToChat(target);

    await sleep(60000);
    await client.say(target, "Starting mining...");

    await sleep(120000);
    await client.say(target, "Mining finished!");

    try {
        await Promise.all(config.ChattersThatWantToPlay.map(async (record) => {
            const [playerName, blocks, skinName] = record.split(",");
            const bonusPoints = 100;

            console.log(`Player Name: ${playerName.trim()}, Blocks: ${blocks.trim()}, Skin Name: ${skinName.trim()}`);
            console.log("-----------------------");

            await changeStreamElementsPoints(target, playerName.trim(), parseInt(blocks.trim(), 10) + bonusPoints);
        }));
    } catch (error) {
        console.error("Error awarding prizes:", error);
    }

    isMineTogetherModeActive = false;
    await clearMinerList(target);
};

export const clearMinerList = async (target: string) => {
    try {
        const config: MineTogetherConfig = yaml.load(fs.readFileSync(botConfig.configFilePath, "utf8")) as MineTogetherConfig;
        config.ChattersThatWantToPlay = [];
        fs.writeFileSync(botConfig.configFilePath, yaml.dump(config));
        await client.say(target, "Miner list cleared.");
    } catch (error) {
        console.error("Error clearing miner list:", error);
    }
};


export const sleep = async (ms: number): Promise<void> => new Promise((resolve) => setTimeout(resolve, ms));

const startingMessageToChat = async (target: string) => {
    await client.say(target, "***")
    await client.say(target, "Mine Together Mode Activated")
    await client.say(target, `Type "!mine 100 Notch" to play`)
    await client.say(target, "Note: !mine <NumberOfBlocksToMine> <MinecraftPlayerName>")
}

(async () => {
    if (typeof require !== 'undefined' && require.main === module) {
        const {default: bot} = await import('./bot');
        await bot.client.connect();
    }
})();

export default {
    handleMineCommand,
    parseMineCommand,
    getStreamElementsPoints,
    changeStreamElementsPoints,
    addMinerToConfig,
    checkMineTogetherModeStatus,
    startMineTogetherMode,
    clearMinerList,
    sleep,
    client,
    isMineTogetherModeActive
};
