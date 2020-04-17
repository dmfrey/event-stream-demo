export const addTodoOffline = content => ({
    type: "ADD_TODO",
    payload: {
        content
    },
    meta: {
        offline: {
            effect: {
                url: "/api/sample",
                method: "POST",
                body: `name=${content}`,
                headers: { "content-type": "application/json" }
            },

            commit: { type: "ADD_TODO", meta: { content } },
            rollback: { type: "ADD_TODO_ROLLBACK", meta: { content } }
        }
    }
});

export const listWorkordersOffline = () => ({
    type: "LIST_WORKORDERS",
    meta: {
        offline: {
            effect: {
                url: "http://localhost:9090/workorders",
                method: "GET",
                headers: { "content-type": "application/json" }
            },

            commit: { type: "LIST_WORKORDERS" },
            rollback: { type: "LIST_WORKORDERS_ROLLBACK" }

        }
    }
});

export const createWorkorderOffline = content => ({
    type: "NEW_WORKORDER",
    payload: {
        content
    },
    meta: {
        offline: {
            effect: {
                url: "http://localhost:9090/workorders",
                method: "POST",
                body: content,
                headers: { "content-type": "application/json" }
            },

            commit: { type: "NEW_WORKORDERS", meta: { content } },
            rollback: { type: "NEW_WORKORDERS_ROLLBACK", meta: { content } }

        }
    }
});

