
import { openDB } from 'https://unpkg.com/idb?module';
import Repository from './repository.js';

export default new Repository( openDB );