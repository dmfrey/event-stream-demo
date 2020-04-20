
import { openDB } from '../lib/idb/index.js';
import Repository from './repository.js';

export default new Repository( openDB );