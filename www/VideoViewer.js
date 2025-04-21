var exec = require('cordova/exec');

const PLUGIN_NAME = "VideoViewer";

exports.show = function (args, success, error) {
    exec(success, error, PLUGIN_NAME, 'show', [args]);
};
