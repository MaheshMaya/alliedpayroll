import type { Meta, StoryObj } from "@storybook/react-vite";
import { Download, Trash2 } from "lucide-react";

import { Button } from "./button";

const meta = {
  title: "Shared/Button",
  component: Button,
  args: {
    children: "Approve run",
    variant: "primary",
    size: "md",
    disabled: false,
  },
  argTypes: {
    variant: {
      control: "select",
      options: ["primary", "secondary", "ghost", "danger"],
    },
    size: { control: "select", options: ["sm", "md", "lg", "icon"] },
  },
} satisfies Meta<typeof Button>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {};

export const Secondary: Story = {
  args: { variant: "secondary", children: "Save draft" },
};

export const Ghost: Story = { args: { variant: "ghost", children: "Cancel" } };

export const Danger: Story = {
  args: { variant: "danger", children: "Terminate employee" },
};

export const Disabled: Story = { args: { disabled: true } };

export const WithIcon: Story = {
  args: { variant: "secondary", children: undefined },
  render: (args) => (
    <Button {...args}>
      <Download aria-hidden="true" />
      Export paysheet
    </Button>
  ),
};

export const IconOnly: Story = {
  args: { variant: "ghost", size: "icon", children: undefined },
  render: (args) => (
    <Button {...args} aria-label="Delete row">
      <Trash2 aria-hidden="true" />
    </Button>
  ),
};

export const Sizes: Story = {
  args: { children: undefined },
  render: (args) => (
    <div className="flex items-center gap-3">
      <Button {...args} size="sm">
        Small
      </Button>
      <Button {...args} size="md">
        Medium
      </Button>
      <Button {...args} size="lg">
        Large
      </Button>
    </div>
  ),
};

export const LongTamilLabel: Story = {
  args: { children: "சம்பளப் பட்டியலை அங்கீகரிக்கவும்" },
};
