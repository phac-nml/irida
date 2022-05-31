/* eslint-disable @typescript-eslint/no-explicit-any */
interface Dictionary {
    [key: string]: string;
}

interface IridaWindow extends Window {
    translations?: Dictionary[];
    TL?: any;
    PAGE?: any;
    IRIDA?: any;
    GALAXY?: any;
    project?: any;
}