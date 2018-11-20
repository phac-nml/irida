import React, { Component } from "react";
import { render } from "react-dom";
import { getCart, getProjectsInCart } from "../../apis/cart/cart";
import { List, Skeleton } from "antd";

// class CartProject extends React.Component {
//   state = { loading: true };
//
//   componentDidMount() {
//     const { id } = this.props;
//     console.log(id);
//   }
//
//   render() {
//     const { loading } = this.state;
//     const { id } = this.props;
//
//     return (
//       <List.Item key={id} actions={[<a>Remove</a>]}>
//         <Skeleton loading={loading} active>
//           <List.Item.Meta
//             title={this.state.title}
//             description={<Skeleton active={true} title={false} />}
//           />
//         </Skeleton>
//       </List.Item>
//     );
//   }
// }

function EmptyCartState() {
  return;
}

class Cart extends Component {
  state = { projects: [] };
  componentDidMount() {
    getProjectsInCart().then(data => {
      this.setState({ projects: data });
    });
  }

  render() {
    const { projects } = this.state;
    return (
      <List>
        {projects.map(id => (
          <CartProject key={id} id={id} />
        ))}
      </List>
    );
  }
}

render(<Cart />, document.querySelector("#root"));
