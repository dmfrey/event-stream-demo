
import repository from './repository';

import { registerMenu } from "./menu.js";
import { registerFavorites } from "./favorites.js";
import { registerControl} from "./navigator.js";
import { registerWorkorders } from "./workorders.js";
import { registerOfflineQueue } from "./offline-queue.js";

const app = async () => {

    let db = repository;

    registerMenu();
    registerFavorites();
    registerControl();
    registerWorkorders();
    registerOfflineQueue();

};

document.addEventListener( 'DOMContentLoaded', app );