import { Badge, Button } from "antd";
import React from "react";
import { getCartCount } from "../../../../apis/cart/cart";
import { SPACE_MD } from "../../../../styles/spacing";
import { CART } from "../../../../utilities/events-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconShoppingCart } from "../../../icons/Icons";

/**
 * React component to display the cart icon and current counts in the
 * IU header.
 * @returns {JSX.Element}
 * @constructor
 */
export function CartLink() {
  const [count, setCount] = React.useState(0);
  /*
  If we are inside a galaxy session then the cart should direct to the galaxy.
  TODO: Move this logic to the cart not the main navigation
   */
  const inGalaxy = typeof window.GALAXY !== "undefined";

  function updateCount(e) {
    const { count: newCount } = e.detail;
    setCount(newCount);
  }

  // Initialize cart here
  React.useEffect(() => {
    getCartCount().then(setCount);
  }, []);

  React.useEffect(() => {
    document.addEventListener(CART.UPDATED, updateCount, false);
    return () => document.removeEventListener(CART.UPDATED, updateCount, false);
  }, []);

  return (
    <div style={{ padding: `0  ${SPACE_MD}` }}>
      <Button
        type="link"
        className="t-cart-count"
        href={setBaseUrl(`/cart/${inGalaxy ? "galaxy" : "pipelines"}`)}
        icon={
          <Badge count={count} offset={[10, -5]}>
            <IconShoppingCart data-count={count} />
          </Badge>
        }
      />
    </div>
  );
}
