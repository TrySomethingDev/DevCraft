const tmi = require("tmi.js");
const yaml = require("js-yaml");
const fs = require("fs");
// Define configuration options

//To get the token I am using the twitch Command Line Tool
//  twitch token -u -s 'chat:edit chat:read'

const opts = {
  identity: {
    username: "<YourTwitchUserNameHere>",
    password: "<YourTwitchAppUserTokenWith chat:edit and chat:read scopes>",
  },
  channels: ["<YourChannelName>"],
};

const streamElementsToken =
  "<StreamElements API Token>";

const streamElementsChannelId = "<Channel ID goes here>"; // Lookes something like this 736d4840273adbf10b5b6541, can be found on your stream elements profile page.

// Create a client with our options
const client = new tmi.client(opts);

// Register our event handlers (defined below)
client.on("message", onMessageHandler);
client.on("connected", onConnectedHandler);

// Connect to Twitch:
client.connect();

let IsMineTogetherModeAcivated = false;

 // Your Minecraft Server Plugin File Path for TrySomethingDevAmazingPlugin Config.yml Here is an example C:\\Files\\Servers\\PaperServerDev1\\plugins\\TrySomethingDevAmazingPlugin\\config.yml
let path =
  "<Plugin Config.YML Path">;

  const adminName = "<Your Twitch Channel Name>";

// Called every time a message comes in
async function onMessageHandler(target, context, msg, self) {
  if (self) return; // Ignore messages from the bot

  const commandName = msg.trim();

  CheckMineTogetherModeStatus(target, msg);
  ClearMinerListCommand(commandName, context, msg, target);
  await AddMinerCommand(commandName, context, msg, target);

  async function AddMinerCommand(command, context, msg, target) {
    var str = commandName;
    var prefix = "!MINE";

    if (str.toUpperCase().startsWith(prefix)) {
      // do something if the string starts with "!Mine"
      // !mine
      // !mine Notch 100
      // !mine 100
      // !mine Notch
      // !mine 100 Notch

      let my_string = commandName;
      let { playerName, MinecraftSkin, NumberOfBlocksToMine } =
        ParseAddMinerCommandFromChat(my_string, commandName, context);

      if (!IsMineTogetherModeAcivated) {
        client.say(
          target,
          "Cannot add you to miner list, because 'Mine Together Mode' is not currently active."
        );
        return;
      }

      //Check StreamElementsToMakeSureThisPlayerHasEnough Points

      //If they have enough points we should remove those points from their account.

      async function getPointsPlayersHasOnStreamElements(playerName) {
        const axios = require("axios");

        const url = `https://api.streamelements.com/kappa/v2/points/${streamElementsChannelId}/${playerName}`;

        await axios
          .get(url)
          .then((response) => {
            // Extract the 'points' property from the response data
            const points = response.data.points;

            // Now you can use the 'points' variable as needed
            console.log("Points:", points);
            NextStepAfterGettingPoints(
              playerName,
              points,
              NumberOfBlocksToMine,
              context,
              MinecraftSkin,
              target
            );
          })
          .catch((error) => {
            console.error("Error fetching data:", error.message);
          });
      }

      let points = await getPointsPlayersHasOnStreamElements(playerName);

      console.log(`* Executed ${commandName} command`);
    } else {
      console.log(`* Unknown command ${commandName}`);
    }
  }

  function NextStepAfterGettingPoints(
    playerName,
    points,
    NumberOfBlocksToMine,
    context,
    MinecraftSkin,
    target
  ) {
    console.log(`${playerName} has ${points}`);
    if (points >= NumberOfBlocksToMine) {
      AddOrRemovePointsForUserOnStreamElements(
        target,
        playerName,
        NumberOfBlocksToMine * -1
      );

      AddMinerToMineTogetherModeListYML(
        context,
        NumberOfBlocksToMine,
        MinecraftSkin,
        target
      );
    } else if (points > 0) {
      //use up rest of points
      AddOrRemovePointsForUserOnStreamElements(target, playerName, points * -1);
      AddMinerToMineTogetherModeListYML(context, points, MinecraftSkin, target);
    } else {
      //This player has no points
      client.say(
        target,
        `Sorry ${playerName} you do not have any points. Try again later.`
      );
    }
  }
}

function AddOrRemovePointsForUserOnStreamElements(target, playerName, points) {
  //If you pass in a positive number it adds points
  //if you pass in a negative number it subtracts points

  const axios = require("axios");

  client.say(target, `Changed ${playerName} point balance: ${points} points.`);

  const url = `https://api.streamelements.com/kappa/v2/points/${streamElementsChannelId}/${playerName}/${points}`;
  const headers = {
    Accept: "application/json; charset=utf-8",
    Authorization: `Bearer ${streamElementsToken}`,
    "Content-Type": "application/json",
  };

  axios
    .put(url, {}, { headers })
    .then((response) => {
      console.log("Response:", response.data);
    })
    .catch((error) => {
      console.error("Error:", error.message);
    });
}

function AddMinerToMineTogetherModeListYML(
  context,
  NumberOfBlocksToMine,
  MinecraftSkin,
  target
) {
  let doc = yaml.load(fs.readFileSync(path, "utf8"));

  doc.ChattersThatWantToPlay.push(
    context["display-name"] +
      "," +
      NumberOfBlocksToMine.toString() +
      "," +
      MinecraftSkin.toString()
  );
  fs.writeFile(path, yaml.dump(doc), (err) => {
    if (err) {
      console.log(err);
    }
    client.say(
      target,
      context["display-name"] + `, your name has been added to the list.`
    );
  });
}

function ParseAddMinerCommandFromChat(my_string, commandName, context) {
  let spaceCount = my_string.split(" ").length - 1;

  let splitCommand = commandName.split(/[ ,]+/);

  let playerName = context["display-name"];
  let NumberOfBlocksToMine = "24";
  let MinecraftSkin = "Player";
  let secondArg = 0;
  let thirdArg = 0;

  switch (spaceCount) {
    case 0:
      //This is just the mine command
      break;
    case 1:
      //Its mine command with a skin or a number specified
      secondArg = splitCommand[1];
      if (!isNaN(secondArg)) {
        //Then it is our number of blocks
        NumberOfBlocksToMine = secondArg;
      } else {
        //Then it is the minecraft skin
        MinecraftSkin = secondArg;
      }
      break;
    case 2:
      //Its mine command with skin and number speicied
      secondArg = splitCommand[1];
      thirdArg = splitCommand[2];
      if (!isNaN(secondArg)) {
        //Then it is our number of blocks
        NumberOfBlocksToMine = secondArg;
      } else {
        //Then it is the minecraft skin
        MinecraftSkin = secondArg;
      }

      if (!isNaN(thirdArg)) {
        //Then it is our number of blocks
        NumberOfBlocksToMine = thirdArg;
      } else {
        //Then it is the minecraft skin
        MinecraftSkin = thirdArg;
      }

      break;
    default:
      console.log("Default Case");
      //Unkown Command
      break;
  }
  return { playerName, MinecraftSkin, NumberOfBlocksToMine };
}

function ClearMinerListCommand(commandName, context, msg, target) {
  if (
    commandName.toUpperCase() === "!MINECLEAR" &&
    context["display-name"] === adminName
  ) {
    ClearMinerList(msg, target);
  }
}

// Function called when the "dice" command is issued
function rollDice() {
  const sides = 6;
  return Math.floor(Math.random() * sides) + 1;
}

// Called every time the bot connects to Twitch chat
function onConnectedHandler(addr, port) {
  console.log(`* Connected to ${addr}:${port}`);
}

function ClearMinerList(msg, target) {
  let doc = yaml.load(fs.readFileSync(path, "utf8"));
  doc.ChattersThatWantToPlay = [];

  fs.writeFile(path, yaml.dump(doc), (err) => {
    if (err) {
      console.log(err);
    }

    client.say(target, `Cleared Miner List`);
  });
}

function SetRequestedAction(value) {
  let doc = yaml.load(fs.readFileSync(path, "utf8"));

  doc.General.RequestedAction = value;
  fs.writeFile(path, yaml.dump(doc), (err) => {
    if (err) {
      console.log(err);
    }
  });
}

async function CheckMineTogetherModeStatus(target, msg) {
  let doc = yaml.load(fs.readFileSync(path, "utf8"));

  if (!doc) {
    console.log(
      "Minecraft MineTogetherMode Config File is not loading or does not exist"
    );
    return;
  }

  if (!doc.General) {
    console.log("doc general is not");
    return;
  }

  if ("StartRequested" === doc.General.RequestedAction) {
    client.say(target, `Detected that a Start has been requested...`);

    SetRequestedAction("PlayerSignUpOpen");

    IsMineTogetherModeAcivated = true;

    StartingMessageToChat(target);

    //Set 60 second timer
    await sleep(60000);

    //SetRequestedAction("PlayerSignUpClosed");
    client.say(target, `60 Seconds have passed. Starting Mining...`);

    //SetRequestedAction("MiningStarted");

    await sleep(120000);
    //SetRequestedAction("MiningFinished");
    client.say(target, `2 minutes have passed Mining is now over.`);

    //SetRequestedAction("PrizesAwarding");

    // Read the YAML file
    try {
      const fileContents = fs.readFileSync(path, "utf8");
      const config = yaml.load(fileContents);

      // Check if ChattersThatWantToPlay exists in the YAML file
      if (
        config.ChattersThatWantToPlay &&
        Array.isArray(config.ChattersThatWantToPlay)
      ) {
        // Iterate through each record
        config.ChattersThatWantToPlay.forEach((record) => {
          // Split each record into three fields by commas
          const [playerName, blocks, skinName] = record.split(",");

          // Display the fields
          console.log("Player Name:", playerName.trim());
          console.log("Blocks:", blocks.trim());
          console.log("Skin Name:", skinName.trim());
          console.log("-----------------------");

          //Need to figure out the actual amount to award at this time we are just going to award each player 100 points.
          AddOrRemovePointsForUserOnStreamElements(
            target,
            playerName,
            parseInt(blocks) + 100
          );
        });
      } else {
        console.log(
          "ChattersThatWantToPlay not found or not an array in the YAML file."
        );
      }
    } catch (error) {
      console.error("Error reading the YAML file:", error.message);
    }

    //SetRequestedAction("PrizesAwarded");

    //SetRequestedAction("Complete");

    // SetRequestedAction("NotStarted");

    IsMineTogetherModeAcivated = false;
    ClearMinerList(msg, target);
  }

  // Define a sleep function that returns a promise
  function sleep(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
function StartingMessageToChat(target) {
  client.say(target, `***`);
  client.say(target, `Mine Together Mode Activated`);
  client.say(target, `Type "!mine 100 Notch" to play`);
  client.say(
    target,
    "Note: !mine <NumberOfBlocksToMine> <MinecraftPlayerName>"
  );
  client.say(target, "Note2: It costs 1 channel point per block to mine.");
}
