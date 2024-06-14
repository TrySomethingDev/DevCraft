import {defineConfig} from "vite";
import dotenv from "dotenv";

dotenv.config();

export default defineConfig({
    define: {
        "process.env": {
            TWITCH_USERNAME: JSON.stringify(process.env.TWITCH_USERNAME),
            TWITCH_PASSWORD: JSON.stringify(process.env.TWITCH_PASSWORD),
            TWITCH_CHANNEL_NAME: JSON.stringify(process.env.TWITCH_CHANNEL_NAME),
            STREAMELEMENTS_TOKEN: JSON.stringify(process.env.STREAMELEMENTS_TOKEN),
            STREAMELEMENTS_CHANNEL_ID: JSON.stringify(process.env.STREAMELEMENTS_CHANNEL_ID),
            CONFIG_FILE_PATH: JSON.stringify(process.env.CONFIG_FILE_PATH)
        }
    },
    build: {
        target: "esnext",
        rollupOptions: {
            external: ["tmi.js", "axios", "fs", "yaml"],
            output: {
                format: "cjs",
            }
        }
    },
    server: {
        hmr: {
            host: "localhost"
        }
    },
    plugins: [],
    optimizeDeps: {
        esbuildOptions: {
            target: "esnext"
        },
        entries: ['bot.ts']
    }
});
