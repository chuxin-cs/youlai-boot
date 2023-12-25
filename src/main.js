import Vue from "vue";
import App from "./App";
import "./uni.promisify.adaptor";

Vue.config.productionTip = false;

App.mpType = "app";

function createApp() {
    return new Vue({
        ...App,
    });
}

const app = createApp();
app.$mount();
