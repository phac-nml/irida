/**
 * Translations for refresh token based on time in seconds.
 * @type {({text: string, value: number})[]}
 */
export const REFRESH_TOKEN_VALIDITY = [
  {
    value: 0,
    text: i18n("constants.clients.refreshTokenValidity.0"),
  },
  {
    value: 604800,
    text: i18n("constants.clients.refreshTokenValidity.604800"),
  },
  {
    value: 2592000,
    text: i18n("constants.clients.refreshTokenValidity.2592000"),
  },
  {
    value: 7776000,
    text: i18n("constants.clients.refreshTokenValidity.7776000"),
  },
  {
    value: 15552000,
    text: i18n("constants.clients.refreshTokenValidity.15552000"),
  },
];

/**
 * Translation for token validity based on seconds.
 * @type {({text: string, value: number})[]}
 */
export const TOKEN_VALIDITY = [
  {
    value: 1800,
    text: i18n("constants.clients.tokenValidity.1800"),
  },
  {
    value: 3600,
    text: i18n("constants.clients.tokenValidity.3600"),
  },
  {
    value: 7200,
    text: i18n("constants.clients.tokenValidity.7200"),
  },
  {
    value: 21600,
    text: i18n("constants.clients.tokenValidity.21600"),
  },
  {
    value: 43200,
    text: i18n("constants.clients.tokenValidity.43200"),
  },
  {
    value: 86400,
    text: i18n("constants.clients.tokenValidity.86400"),
  },
  {
    value: 172800,
    text: i18n("constants.clients.tokenValidity.172800"),
  },
];
