import React from 'react';

const WorkordersList = workorders => (
    <ul>
        {workorders.map(wo => (
            <li key={wo.workorderId}>
                <div>{wo.workorderId}</div>
                <div>{wo.title}</div>
            </li>
        ))}
    </ul>
);

export default WorkordersList;