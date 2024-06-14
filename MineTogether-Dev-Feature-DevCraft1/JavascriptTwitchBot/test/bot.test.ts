import {describe, it, expect, vi, beforeEach} from 'vitest';
import axios from 'axios';
import * as tmi from 'tmi.js';
import {
    parseMineCommand,
    getStreamElementsPoints,
    changeStreamElementsPoints,
} from '../bot';

describe('Bot Tests', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.restoreAllMocks();
    });


    it('should parse mine command correctly', () => {
        const command = '!mine 100 Notch';
        const context = {"display-name": 'user'} as tmi.ChatUserstate;
        const result = parseMineCommand(command, context);
        expect(result).toEqual({
            playerName: 'user',
            minecraftSkin: 'Notch',
            blocksToMine: 100
        });
    });

    it('should get StreamElements points', async () => {
        const axiosGetMock = vi.spyOn(axios, 'get');
        axiosGetMock.mockResolvedValue({data: {points: 1000}});

        const username = "testuser";
        const points = await getStreamElementsPoints(username);
        expect(points).toBe(1000);
        expect(axios.get).toHaveBeenCalledWith(
            `https://api.streamelements.com/kappa/v2/points/${process.env.STREAMELEMENTS_CHANNEL_ID}/${username}`
        );
    });

    it("should change points using StreamElements API", async () => {
        const target = "target";
        const username = "testuser";
        const points = -100;
        const axiosGetMock = vi.spyOn(axios, 'put');
        axiosGetMock.mockResolvedValue({data: {points: 1000}});

        await changeStreamElementsPoints(target, username, points);

        expect(axios.put).toHaveBeenCalledWith(
            `https://api.streamelements.com/kappa/v2/points/${process.env.STREAMELEMENTS_CHANNEL_ID}/${username}/${points}`,
            {},
            {headers: {Authorization: `Bearer ${process.env.STREAMELEMENTS_TOKEN}`}}
        );
    });
});
