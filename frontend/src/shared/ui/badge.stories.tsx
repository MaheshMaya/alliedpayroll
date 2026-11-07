import type { Meta, StoryObj } from "@storybook/react-vite";

import { Badge } from "./badge";

const meta = {
  title: "Shared/Badge",
  component: Badge,
  args: { children: "Draft", status: "neutral", dot: false },
  argTypes: {
    status: {
      control: "select",
      options: ["neutral", "success", "warning", "danger", "info"],
    },
  },
} satisfies Meta<typeof Badge>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Neutral: Story = {};

export const Success: Story = {
  args: { status: "success", children: "Approved", dot: true },
};

export const Warning: Story = {
  args: { status: "warning", children: "2 unmarked", dot: true },
};

export const Danger: Story = {
  args: { status: "danger", children: "Locked", dot: true },
};

export const Info: Story = {
  args: { status: "info", children: "In approval", dot: true },
};

export const AllStatuses: Story = {
  args: { children: undefined },
  render: () => (
    <div className="flex flex-wrap items-center gap-2">
      <Badge status="neutral" dot>
        Draft
      </Badge>
      <Badge status="info" dot>
        In approval
      </Badge>
      <Badge status="success" dot>
        Approved
      </Badge>
      <Badge status="warning" dot>
        2 unmarked
      </Badge>
      <Badge status="danger" dot>
        Locked
      </Badge>
    </div>
  ),
};

export const LongTamilLabel: Story = {
  args: { status: "success", dot: true, children: "அங்கீகரிக்கப்பட்டது" },
};
