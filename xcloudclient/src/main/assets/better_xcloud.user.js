// ==UserScript==
// @name         Better XCloud (Embedded)
// @namespace    https://github.com/
// @version      1.0.0
// @description  Placeholder script loaded by the Better XCloud Android client. Replace this file with the official script for production use.
// ==/UserScript==

(function () {
    'use strict';

    const config = window.__betterXcloudConfig || {};
    console.log('[BetterXcloudClient] Loaded configuration', config);

    // The actual Better XCloud browser mod performs extensive DOM manipulation here. For the
    // embedded client we expose a few hooks that the official script can use.
    window.BetterXcloudNativeBridge = {
        getControllerKernel() {
            return config.kernel || 'native';
        },
        getKeyMapping() {
            return config.mapping || {};
        },
        isKeepAwakeEnabled() {
            return Boolean(config.keepAwake);
        }
    };
})();
