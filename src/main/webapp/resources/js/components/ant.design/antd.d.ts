interface Option {
    value: string;
    label: string;
}

interface CascaderOption extends Option {
    children: CascaderOption[];
}