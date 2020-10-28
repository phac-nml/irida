import {
  deepCopy,
  formatDefaultPipelineName,
  formatParametersWithOptions,
  isTruthy,
} from "./launch-utilities";

test("Should format the pipeline name based on its type and date", () => {
  expect(formatDefaultPipelineName("bio_hansel", 1590093988318)).toBe(
    "bio_hansel__5-21-2020"
  );
});

test("Should determine of a list of parameters passed is truthy", () => {
  expect(isTruthy([{ value: "true" }, { value: "false" }])).toBeTruthy();
  expect(isTruthy([{ value: true }, { value: false }])).toBeTruthy();
  expect(
    isTruthy([
      { value: "Fun times at Richmond High" },
      { value: "Terminator 2" },
    ])
  ).toBeFalsy();
  expect(
    isTruthy([{ value: "Fun times at Richmond High" }, { value: false }])
  ).toBeFalsy();
});

test("Should format parameters with options with proper values and their UI type", () => {
  const parameters = [
    {
      options: [{ value: "true" }, { value: false }],
    },
    {
      options: [
        { value: "Fun times at Richmond High" },
        { value: "Terminator 2" },
      ],
    },
    {
      options: [
        { value: "Fun times at Richmond High" },
        { value: "Terminator" },
        { value: "Terminator 2" },
        { value: "Hercules Goes Bananas" },
        { value: "Kindergarten Cop" },
        { value: "Twins" },
        { value: "Commando" },
        { value: "Pumping Iron" },
      ],
    },
  ];
  const response = formatParametersWithOptions(parameters);
  expect(response[0].type).toBe("checkbox");
  response[0].options.forEach((option) =>
    expect(typeof option.value).toBe("boolean")
  );

  expect(response[1].type).toBe("radio");
  response[1].options.forEach((option) =>
    expect(typeof option.value).not.toBe("boolean")
  );

  expect(response[2].type).toBe("select");
  response[2].options.forEach((option) =>
    expect(typeof option.value).not.toBe("boolean")
  );
});

test("Should be able to deep copy an array of object", () => {
  const original = [{ a: 1 }, { b: 2 }];
  const copy = original;
  const deeplyCopied = deepCopy(original);
  deeplyCopied[0].a = 1000;
  expect(original);
  expect(original[0].a).toBe(1);
  expect(deeplyCopied[0].a).toBe(1000);
  copy[1].b = 50;
  expect(original[1].b).toBe(50);
  expect(deeplyCopied[1].b).not.toBe(50);
});

test("Should be able to deep copy an object", () => {
  const original = { a: 1, b: { c: 1 }, d: [1, 2, 3, 4] };
  const copy = original;
  const deeplyCopied = deepCopy(original);

  original.a = 1000;
  expect(copy.a).toBe(1000);
  expect(deeplyCopied.a).not.toBe(1000);
  original.d[0] = "jelly";
  expect(copy.d[0]).toBe("jelly");
  expect(deeplyCopied.d[0]).not.toBe("jelly");
});
