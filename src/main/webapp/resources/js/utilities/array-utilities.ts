/**
 * Splits an array into multiple, smaller arrays of the given size
 *
 * @param items the array to be chunked
 * @param size the size of the chunk
 *
 * @returns a chunked array
 */
export function chunkArray(items: any[], size: number) {
  const chunks = [];
  items = [].concat(...items);

  while (items.length) {
    chunks.push(items.splice(0, size));
  }

  return chunks;
}
