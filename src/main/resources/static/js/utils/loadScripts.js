export default function loadScripts( urls, yeyCallback, neyCallback ) {

    let count = urls.length;
    let errored = false;

    if( urls.length == 0 ) return yeyCallback();

    urls.forEach( function( url ) {

        let script = document.createElement( 'script' );
        script.onload = function() {

            if( errored ) return;
            if( !--count ) yeyCallback();

        };

        script.onerror = function() {

            if( errored ) return;

            neyCallback();
            errored = true;

        };

        script.src = url;

        document.head.insertBefore( script, document.head.firstChild );

    });

};