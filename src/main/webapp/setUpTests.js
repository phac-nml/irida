import { JSDOM } from "jsdom";

const dom = new JSDOM();
global.document = dom.window.document;
global.window = dom.window;

export const FAKE_CONTEXT_PATH = "/foobar";

// Mock the context path
global.document.documentElement.dataset.context = FAKE_CONTEXT_PATH;
