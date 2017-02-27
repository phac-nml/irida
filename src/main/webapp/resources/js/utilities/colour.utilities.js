/**
 * Generate a random hexadecimal colour
 * @return {string} random colour
 */
export const getRandomColour = () => `#${Math.random().toString(16).slice(-6)}`;
