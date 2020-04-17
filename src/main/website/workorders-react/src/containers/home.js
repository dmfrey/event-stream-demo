import React, { useMemo, useEffect, useState } from "react";
import { useSelector, useDispatch, shallowEqual } from "react-redux";

// local import
import { listWorkordersOffline, createWorkorderOffline } from "../redux/actions/app";

/**
 * Home Component of offline-first Boilerplate.
 * @name Home
 */

function Home() {

    // redux hook methods
    const state = useSelector( state => {
        return {
            workorders: state.app.workorders,
            status: state.app.status
        };
    }, shallowEqual );

    // const setWorkordersFunc = e => {
    //     dispatch(listWorkordersOffline());
    // };

    const dispatch = useDispatch();
    // redux hook methods

    // const [workorder, setWorkorder] = useState("");

    // const setWorkorderFunc = json => {
    //     dispatch( createWorkorderOffline({ title:"test title", targetNode:"local" } ) );
    //     dispatch( listWorkordersOffline() );
    // };

    return (
        <>
            {JSON.stringify(state.workorders)}
            {/*{state.workorders.map( workorder => <pre>{workorder}</pre> )}*/}
            {/*<WorkordersList workorders={typeof state.workorders !== undefined ? state.workorders : [{workorderId: "-", title: "No Work Orders to display!"}]} />*/}
        </>
    );
}

export default Home;
