
import repository from './repository/index.js';

import { registerMenu } from "./menu.js";
import { registerFavorites } from "./favorites.js";
import { registerControl} from "./navigator.js";
import { registerWorkorders } from "./workorders.js";
import { registerOfflineQueue } from "./offline-queue.js";
import { registerWebsocket } from "./websocket.js";

const app = async () => {

    let db = repository;

    registerWebsocket();

    registerMenu();
    registerFavorites();
    registerControl();
    registerWorkorders();
    registerOfflineQueue();

};

document.addEventListener( 'DOMContentLoaded', app );