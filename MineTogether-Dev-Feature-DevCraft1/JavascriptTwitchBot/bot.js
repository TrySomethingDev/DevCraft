/**
 * TMI-based Twitch bot for "Mine Together Mode"
 * - Adds players to a "mining list"
 * - Deducts their points using the StreamElements API
 * - Handles when the mode starts and ends
 */

const tmi = require("tmi.js");
const yaml = require("js-yaml");
const fs = require("fs");
const axios = require("axios");

/**
 * Twitch client configuration
 * 'password' = OAuth Token from the Twitch CLI (token with chat:edit and chat:read scopes)
 */
const twitchOptions = {
  identity: {
    username: "<YourTwitchUserNameHere>",
    password: "<YourTwitchAppUserTokenWithChatScopes>",
  },
  channels: ["<YourTwitchChannelName>"],
};

/**
 * StreamElements configuration
 */
const streamElementsToken = "<YourStreamElementsAPIToken>";
const streamElementsChannelId = "<YourStreamElementsChannelID>"; // e.g. 736d4840273adbf10b5b6541

/**
 * Path to your plugin's config.yml file
 * Example: C:\\Files\\Servers\\PaperServerDev1\\plugins\\TrySomethingDevAmazingPlugin\\config.yml
 */
const pluginConfigPath = "<PathToYourConfigYML>";

/**
 * The name of the channel owner (admin),
 * who is authorized to execute the !mineclear command
 */
const adminName = "<YourTwitchChannelName>";

/** Create a TMI client */
const client = new tmi.Client(twitchOptions);

/** Flag indicating if "Mine Together Mode" is active */
let isMineTogetherModeActive = false;

/** Connect to Twitch chat and register event handlers */
client.connect();
client.on("message", onMessageHandler);
client.on("connected", onConnectedHandler);

/* ==============================
   Event Handlers
   ============================== */

/**
 * Handler for incoming chat messages
 */
async function onMessageHandler(target, context, msg, self) {
  if (self) return; // Ignore messages from the bot itself

  const commandName = msg.trim();

  // Check or update the status of "Mine Together Mode"
  await checkMineTogetherModeStatus(target);

  // Handle commands
  handleMineClearCommand(commandName, context, target);
  await handleAddMinerCommand(commandName, context, target);
}

/**
 * Handler for successful connection to the Twitch chat
 */
function onConnectedHandler(addr, port) {
  console.log(`* Connected to ${addr}:${port}`);
}

/* ==============================
   Command Handlers
   ============================== */

/**
 * Command: !mineclear
 * Clears the list of miners if executed by the adminName
 */
function handleMineClearCommand(commandName, context, target) {
  if (commandName.toLowerCase() === "!mineclear" && context["display-name"] === adminName) {
    clearMinerList(target);
  }
}

/**
 * Command: !mine <optional params>
 * Adds a user to the mining list
 */
async function handleAddMinerCommand(commandName, context, target) {
  if (!commandName.toLowerCase().startsWith("!mine")) {
    return; // Ignore other commands
  }

  if (!isMineTogetherModeActive) {
    client.say(target, "Cannot add you to the miner list because 'Mine Together Mode' is not currently active.");
    return;
  }

  // Parse parameters from the command
  const { playerName, minecraftSkin, numberOfBlocks } = parseMineCommand(commandName, context);

  // Fetch the player's points from StreamElements
  const points = await getPointsFromStreamElements(playerName);
  if (points == null) {
    client.say(target, `Failed to fetch points for user ${playerName}.`);
    return;
  }

  // Evaluate the player's points and proceed
  evaluatePlayerPoints(playerName, points, numberOfBlocks, context, minecraftSkin, target);

  console.log(`* Executed command: ${commandName}`);
}

/* ==============================
   Core Logic / Helpers
   ============================== */

/**
 * Checks the config to see if Mine Together Mode has been requested.
 * If RequestedAction === "StartRequested", it automatically triggers
 * the sign-up window, countdown, and awarding cycle.
 */
async function checkMineTogetherModeStatus(target) {
  const doc = readYAML(pluginConfigPath);
  if (!doc || !doc.General) {
    console.log("Configuration file not loaded or missing 'General' section.");
    return;
  }

  if (doc.General.RequestedAction === "StartRequested") {
    client.say(target, "Detected that a Start has been requested...");
    setRequestedAction("PlayerSignUpOpen");
    isMineTogetherModeActive = true;

    announceMineTogetherStart(target);

    // Wait 60 seconds for sign-ups
    await sleep(60000);
    client.say(target, "60 seconds have passed. Starting the mining phase...");

    // Example of writing to the config if desired:
    //   setRequestedAction("PlayerSignUpClosed");
    //   setRequestedAction("MiningStarted");

    // Wait 120 seconds for the mining phase
    await sleep(120000);
    client.say(target, "2 minutes have passed. Mining is now over.");

    // Award prizes to participants
    awardPrizesToPlayers(target);

    // End cycle
    isMineTogetherModeActive = false;
    clearMinerList(target);

    // Optionally update the config if needed:
    //   setRequestedAction("MiningFinished");
    //   setRequestedAction("PrizesAwarded");
    //   setRequestedAction("Complete");
    //   setRequestedAction("NotStarted");
  }
}

/**
 * Reads and parses a YAML file from the given path
 */
function readYAML(path) {
  try {
    return yaml.load(fs.readFileSync(path, "utf8"));
  } catch (error) {
    console.error("Error reading YAML file:", error.message);
    return null;
  }
}

/**
 * Writes the data object to a YAML file at the specified path
 */
function writeYAML(path, dataObj) {
  try {
    fs.writeFileSync(path, yaml.dump(dataObj), "utf8");
  } catch (err) {
    console.error("Error writing to YAML file:", err.message);
  }
}

/**
 * Sets doc.General.RequestedAction in the YAML config
 */
function setRequestedAction(value) {
  const doc = readYAML(pluginConfigPath);
  if (!doc || !doc.General) return;

  doc.General.RequestedAction = value;
  writeYAML(pluginConfigPath, doc);
}

/**
 * Announces the start of "Mine Together Mode" in the chat
 */
function announceMineTogetherStart(target) {
  client.say(target, "***");
  client.say(target, "Mine Together Mode Activated!");
  client.say(target, 'Type "!mine 100 Notch" to participate in mining.');
  client.say(target, "Syntax: !mine <numberOfBlocks> <minecraftSkin>");
  client.say(target, "It costs 1 channel point for each block you want to mine.");
}

/**
 * Reads the list of players from 'ChattersThatWantToPlay' in the YAML config
 * and awards them points
 */
function awardPrizesToPlayers(target) {
  const doc = readYAML(pluginConfigPath);
  if (!doc || !doc.ChattersThatWantToPlay) {
    console.log("No 'ChattersThatWantToPlay' section found in the config.");
    return;
  }

  doc.ChattersThatWantToPlay.forEach((record) => {
    const [playerNameRaw, blocksRaw, skinNameRaw] = record.split(",");
    const playerName = (playerNameRaw || "").trim();
    const blocks = parseInt((blocksRaw || "0").trim(), 10) || 0;
    const skinName = (skinNameRaw || "").trim();

    console.log("Awarding player:", playerName, "blocks:", blocks, "skin:", skinName);

    // Example: Add (blocks + 100) points to each participant
    addOrRemovePointsForUser(target, playerName, blocks + 100);
  });
}

/**
 * Clears the mining list in the YAML config
 */
function clearMinerList(target) {
  const doc = readYAML(pluginConfigPath);
  if (!doc) return;

  doc.ChattersThatWantToPlay = [];
  writeYAML(pluginConfigPath, doc);

  client.say(target, "The miner list has been cleared.");
}

/**
 * Fetches the user's points from StreamElements
 */
async function getPointsFromStreamElements(playerName) {
  const url = `https://api.streamelements.com/kappa/v2/points/${streamElementsChannelId}/${playerName}`;

  try {
    const response = await axios.get(url);
    return response.data.points;
  } catch (error) {
    console.error("Error fetching points from StreamElements:", error.message);
    return null;
  }
}

/**
 * Evaluates the player's points and either deducts the required
 * number of blocks, the remainder of their points, or denies them
 * if they have zero
 */
function evaluatePlayerPoints(playerName, points, blocksRequired, context, minecraftSkin, target) {
  console.log(`${playerName} currently has ${points} points.`);

  if (points >= blocksRequired) {
    addOrRemovePointsForUser(target, playerName, -blocksRequired);
    addMinerToList(context, blocksRequired, minecraftSkin, target);
  } else if (points > 0) {
    addOrRemovePointsForUser(target, playerName, -points);
    addMinerToList(context, points, minecraftSkin, target);
  } else {
    client.say(target, `Sorry ${playerName}, you have no points. Try again later.`);
  }
}

/**
 * Adds the player's record to 'ChattersThatWantToPlay'
 */
function addMinerToList(context, blocksRequired, minecraftSkin, target) {
  const doc = readYAML(pluginConfigPath);
  if (!doc) return;

  doc.ChattersThatWantToPlay.push(
      `${context["display-name"]},${blocksRequired},${minecraftSkin}`
  );

  writeYAML(pluginConfigPath, doc);
  client.say(target, `${context["display-name"]}, you have been added to the miner list!`);
}

/**
 * Adds or removes points from a user in StreamElements:
 *  - Positive 'points' => adds points
 *  - Negative 'points' => subtracts points
 */
function addOrRemovePointsForUser(target, playerName, points) {
  client.say(target, `Changing ${playerName}'s point balance by ${points} points.`);

  const url = `https://api.streamelements.com/kappa/v2/points/${streamElementsChannelId}/${playerName}/${points}`;
  const headers = {
    Accept: "application/json; charset=utf-8",
    Authorization: `Bearer ${streamElementsToken}`,
    "Content-Type": "application/json",
  };

  axios
      .put(url, {}, { headers })
      .then((response) => {
        console.log("StreamElements response:", response.data);
      })
      .catch((error) => {
        console.error("Error updating points on StreamElements:", error.message);
      });
}

/**
 * Parses the !mine command, extracting the number of blocks and (optional) Minecraft skin
 */
function parseMineCommand(commandName, context) {
  const splitCmd = commandName.split(/[ ,]+/); // e.g. ["!mine", "100", "Notch"] ...
  let playerName = context["display-name"];
  let numberOfBlocks = 24;
  let minecraftSkin = "Player";

  const args = splitCmd.slice(1); // everything after "!mine"

  if (args.length === 1) {
    // Could be either block count or skin
    if (!isNaN(args[0])) {
      numberOfBlocks = parseInt(args[0], 10);
    } else {
      minecraftSkin = args[0];
    }
  } else if (args.length === 2) {
    // One is a number, the other is a skin name
    const [arg1, arg2] = args;
    if (!isNaN(arg1)) {
      numberOfBlocks = parseInt(arg1, 10);
    } else {
      minecraftSkin = arg1;
    }
    if (!isNaN(arg2)) {
      numberOfBlocks = parseInt(arg2, 10);
    } else {
      minecraftSkin = arg2;
    }
  }

  return { playerName, minecraftSkin, numberOfBlocks };
}

/**
 * Sleep utility function for async operations
 */
function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
