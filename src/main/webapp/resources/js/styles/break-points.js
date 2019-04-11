/**
 * @fileOverview Break points used in .js and .jsx files need to be standardized
 * using the values contained in this file.
 *
 * These values are based on defaults from [ant design grid](https://ant.design/components/grid/)
 * and [Bootstrap 4](https://getbootstrap.com/docs/4.0/layout/overview/#responsive-breakpoints)_
 *
 * PLEASE TO NOT ADD TO OR CHANGE VALUES IN THIS FILE WITHOUT SPEAKING TO THE
 * LEAD ON INTERFACE DESIGN.
 */
const BREAK_POINT_XS = 480;
const BREAK_POINT_SM = 576;
const BREAK_POINT_MD = 992;
const BREAK_POINT_LG = 1200;
const BREAK_POINT_XL = 1600;

const formatMax = size => `max-width: ${size - 1}px`;

export const BREAK_XS_MAX = formatMax(BREAK_POINT_XS);
export const BREAK_SM_MAX = formatMax(BREAK_POINT_SM);
export const BREAK_MD_MAX = formatMax(BREAK_POINT_MD);
export const BREAK_LG_MAX = formatMax(BREAK_POINT_LG);
export const BREAK_XL_MAX = formatMax(BREAK_POINT_XL);
