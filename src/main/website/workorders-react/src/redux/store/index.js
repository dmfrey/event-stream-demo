import { applyMiddleware, createStore, compose } from "redux";
import { offline } from "redux-offline";
import offlineConfig from "redux-offline/lib/defaults";
import thunk from "redux-thunk";
import logger from "redux-logger";
import rootReducer from "../reducers";
import * as localforage from "localforage";
import axios from 'axios';

offlineConfig.persistOptions = { storage: localforage }; // store offline data in indexedDB

// overriding effect
const customConfig = {
    ...offlineConfig,
    effect: (effect, action) => {
        console.dir( effect );
        // console.dir( action );

        return axios( effect )
            .then( (response) => {
                console.dir( response );

                return response;
            });
        // if( effect.method === 'GET' ) {
        //     return axios.get(effect.url)
        //         .then( (response) => {
        //             console.dir( response.data );
        //             return response.data;
        //         });
        // }
        // return axios.post(effect.url, action.payload && action.payload.content);
    }
};

const store = createStore(
    rootReducer,
    {},
    compose( applyMiddleware( thunk, logger ), offline( customConfig ) )
);

export default store;
