const initialState = {
    id: ""
};

export default (state = initialState, action) => {
    // console.dir( action );
    switch (action.type) {
        case "LIST_WORKORDERS":
            return (state = {
                ...state,
                workorders: action.payload
            });
        case "NEW_WORKORDER":
            return (state = {
                ...state,
                workorderItem: action.payload.content
            });
        case "ADD_TODO":
            return (state = {
                ...state,
                todoItem: action.payload.content
            });
        case "Offline/STATUS_CHANGED":
            return (state = {
                ...state,
                status: action.payload.online
            });
        case "ALL_POSTS_SUCCESS":
            return (state = {
                ...state,
                posts: action.payload.posts
            });
        case "UPDATE_POST":
            return (state = {
                ...state,
                post: action.payload.post
            });
        default:
            return state;
    }
};
