import React, { useEffect, useState } from "react";
import { Carousel, Modal } from "antd";
import { getUnreadAnnouncements } from "../../apis/announcements/announcements";
import { IconLeftCircle, IconRightCircle } from "../icons/Icons";

export function Announcements() {
  const [announcements, setAnnouncements] = useState([]);
  const [visible, setVisibility] = useState(true);

  useEffect(() => {
    getUnreadAnnouncements().then((data) => {
      console.log(data.data);
      setAnnouncements(data.data);
    });
  }, []);

  const contentStyle = {
    height: "160px",
    color: "#fff",
    lineHeight: "160px",
    textAlign: "center",
    background: "#364d79",
  };

  function SampleNextArrow(props) {
    const { className, style, onClick } = props;
    return (
      <div
        className={className}
        style={{
          ...style,
          display: "block",
        }}
        onClick={onClick}
      />
    );
  }

  function SamplePrevArrow(props) {
    const { className, style, onClick } = props;
    return (
      <div
        className={className}
        style={{
          ...style,
          display: "block",
        }}
        onClick={onClick}
      />
    );
  }

  const settings = {
    nextArrow: <SampleNextArrow />,
    prevArrow: <SamplePrevArrow />,
  };

  return (
    <>
      <Modal
        title="testing!!!"
        onCancel={() => setVisibility(false)}
        visible={visible}
        footer={null}
      >
        <Carousel {...settings} arrows={true} dots={false}>
          <div>
            <h3 style={contentStyle}>1</h3>
          </div>
          <div>
            <h3 style={contentStyle}>2</h3>
          </div>
          <div>
            <h3 style={contentStyle}>3</h3>
          </div>
          <div>
            <h3 style={contentStyle}>4</h3>
          </div>
        </Carousel>
      </Modal>
    </>
  );
}
