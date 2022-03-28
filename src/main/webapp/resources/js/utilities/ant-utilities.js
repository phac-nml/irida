/**
 * Ant Design Tag preset colors.
 *
 * NOTE: If you add more colors, please ensure that they are legible with text.
 */
const tagColors = [
  "geekblue",
  "magenta",
  "volcano",
  "orange",
  "cyan",
  "gold",
  "green",
  "red",
  "blue",
  "purple",
];

function* getTagColors() {
  let index = 0;
  while (true) {
    yield tagColors[index];
    index = (index + 1) % tagColors.length;
  }
}

/**
 * Generate a new Tag color.  Based off of Ant Design Tag preset color.
 * @returns Valid color for a Tag
 */
export function getNewTagColor() {
  const tagColor = getTagColors();
  return tagColor.next().value;
}
