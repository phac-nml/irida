/**
 * Splits an array into multiple, smaller arrays
 *
 * @param items the array to be chunked
 *
 * @returns a chunked array
 */
export function chunkArray<T>(items: T[]) {
  const size = calculateChunkSize(items.length);
  const clone = [...items];
  const chunks = [];

  while (clone.length) {
    chunks.push(clone.splice(0, size));
  }

  return chunks;
}

/**
 * Calculate the best request chunk size
 *
 * @param arraySize the size of the array
 *
 * @returns the chunk size
 */
function calculateChunkSize(arraySize: number) {
  const MIN = 10;
  const MAX = 100;
  const estimated = Math.ceil(arraySize / 10);
  const isAboveMax = estimated > MAX ? MAX : estimated;
  return estimated < MIN ? MIN : isAboveMax;
}
