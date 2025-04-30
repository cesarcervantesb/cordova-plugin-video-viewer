var exec = require('cordova/exec');

const PLUGIN_NAME = "VideoViewer";

exports.show = function (success, error, args) {
    exec(success, error, PLUGIN_NAME, 'show', [args]);
};
